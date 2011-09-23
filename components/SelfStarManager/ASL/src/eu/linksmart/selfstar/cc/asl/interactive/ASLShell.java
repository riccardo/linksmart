/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @author Mads Ingstrup
 * (c) 2009
 */
package eu.linksmart.selfstar.cc.asl.interactive;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eu.linksmart.selfstar.cc.asl.core.ASLInterpreter;

public class ASLShell {
	
	BundleContext context;
	ASLInterpreter interpreter;
	JTextArea text;
	JFrame f;
	JCheckBoxMenuItem timeExecutionCheckBoxMenuItem;
	
	public ASLShell(BundleContext context, ASLInterpreter interpreter){
		this.context = context;
		this.interpreter = interpreter;
		makeGUI();
	}
	
	private void makeGUI(){
		System.out.println("Launching ASL Shell.");
		f = new JFrame("ASL Shell");
		f.setJMenuBar(makeMenu());
		f.setContentPane(new JPanel(new BorderLayout()));
		text = new JTextArea();
		text.setEditable(true);
		text.setEditable(true);
		Font current = text.getFont();
		text.setFont(new Font("Monospaced",current.getStyle(),current.getSize()-1));
		JScrollPane jsp = new JScrollPane(text);
        JButton evaluate = new JButton("Execute");
        evaluate.setToolTipText("Evaluate the current text as a query");
        evaluate.addActionListener(new ActionListener(){ 
        	public void actionPerformed(ActionEvent arg0) {
				evaluate();
			}
        });
        f.getContentPane().add(jsp,BorderLayout.CENTER);
		f.getContentPane().add(evaluate, BorderLayout.SOUTH);
		setFrameIcon(f);
		//makeButton(buttonp, "show", "Show bundles");
		f.pack();
		f.setSize(600,300);
		f.setLocation(310, 220);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
	private JMenuBar makeMenu() {
		JMenuBar menubar = new JMenuBar();
		JMenuItem menuitem;
		JMenu menu;
		// file menu
		menu = new JMenu("File");
		menuitem = new JMenuItem("Open...");
		menu.add(menuitem);
		menuitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		menuitem = new JMenuItem("Save as...");
		menu.add(menuitem);
		menuitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		menubar.add(menu);
		// tools
		menu = new JMenu("Tools");
		menubar.add(menu);
		menuitem=new JMenuItem("Generate script for this configuration");
		menu.add(menuitem);
		menuitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				generateScriptCurrentConfiguration();
			}
		});
		menuitem=new JMenuItem("Generate script for clearing this configuration");
		menu.add(menuitem);
		menuitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				generateScriptToClearCurrentConfiguration();
			}
		});
		menu = new JMenu("Options");
		menubar.add(menu);
		timeExecutionCheckBoxMenuItem = new JCheckBoxMenuItem("time script execution",false);
		menu.add(timeExecutionCheckBoxMenuItem);
		JCheckBoxMenuItem debug = new JCheckBoxMenuItem("print debug info", interpreter.getDebugEnabled());
		debug.addItemListener(new ItemListener() {			
			
			public void itemStateChanged(ItemEvent e) {
				interpreter.setDebugEnabled(((JCheckBoxMenuItem)e.getItem()).getState());
			}
		});
		menu.add(debug);
		return menubar;
	}

	private boolean ok2ClearBuffer(){
		if (text.getText().length()>0){
			int choice=JOptionPane.showConfirmDialog(f, "This will clear the text buffer.", "Warning", JOptionPane.OK_CANCEL_OPTION);
			if (choice!=JOptionPane.OK_OPTION)
				return false;
		}
		return true;
	}
	
	private void openFile(){
		int returnVal = getFileChooser().showOpenDialog(f);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File file = getFileChooser().getSelectedFile();
	        if (file.canRead())
	          	text.setText(getFileAsString(file));
	        else
	           	JOptionPane.showMessageDialog(f, "No write-access to this file.\nThe file was not saved.");
	    }
	}
	
	static String getFileAsString(File f){
		try {
			BufferedReader bi = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			StringBuffer sb=new StringBuffer(2024);
			String s;
			while((s=bi.readLine()) !=null)
				sb.append(s+"\n");
			bi.close();
			return sb.toString();
		} catch (IOException e){ 
			e.printStackTrace(System.out);
		}
		return "";
	}

	private void saveAs(){
		try {
		int returnVal = getFileChooser().showSaveDialog(f);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getFileChooser().getSelectedFile();
            if (!file.createNewFile()){
    			int choice=JOptionPane.showConfirmDialog(f, "The selected file already exists - continue and overwrite ?", "Warning", JOptionPane.OK_CANCEL_OPTION);
    			if (choice!=JOptionPane.OK_OPTION)
    				return;
            } else { // file was created
            	if (file.canWrite()){
            		FileWriter fw = new FileWriter(file);
            		fw.write(text.getText());
            		fw.close();
            	} else
                    JOptionPane.showMessageDialog(f, "No write-access to this file.\nThe file was not saved.");
            }
        } else {
            JOptionPane.showMessageDialog(f, "File not saved");
        }
		}catch (IOException e){
			e.printStackTrace();
		}

	}
	
	private static JFileChooser jfc;
	private static JFileChooser getFileChooser(){
		if (jfc==null)
			jfc=new JFileChooser();
		return jfc;
	}
	
	private void generateScriptCurrentConfiguration() {
		if (!ok2ClearBuffer())
			return;
		LinkedList<String> init_c=new LinkedList<String>(),
			init_s=new LinkedList<String>(),
			depl_c=new LinkedList<String>(),
			start_s=new LinkedList<String>();
		int i=0;
		String cname,sname,location;
		for (Bundle b: context.getBundles()){
			String symname=b.getSymbolicName();
			if (symname.equals("flamenco_asl"))
				continue; // if a script is executed this will already be installed...
			if (symname.equals("org.apache.felix.framework"))
				continue;
			if (symname==null || symname.length()==0)
				symname="noname"+i++;
			cname="c_"+symname;
			sname="s_"+symname;
			if (b.getLocation().startsWith("file:"))
				location=b.getLocation().substring(5);
			else
				location=b.getLocation();
			init_c.add("init_component("+cname+","+location+");\n");
			depl_c.add("deploy_component(local,"+cname+");\n");
			if (b.getState()==Bundle.ACTIVE){
				init_s.add("init_service(local,"+cname+","+sname+");\n");
				start_s.add("start_service("+sname+");\n");
			}
		}
		LinkedList<String> script=init_c;
		script.add(0, "init_device(local);\n");
		script.add(0, "/*\n * ASL Script generated from a running configuration. \n * Please verify that the services are started in the right order \n */\n");
		
		script.addAll(depl_c);
		script.addAll(init_s);
		script.addAll(start_s);
		StringBuffer b = new StringBuffer();
		for (String op:script)
			b.append(op);
		text.setText(b.toString());
	}
	
	private void generateScriptToClearCurrentConfiguration() {
		if (!ok2ClearBuffer())
			return;
		LinkedList<String> init_c=new LinkedList<String>(),
			init_s=new LinkedList<String>(),
			undepl_c=new LinkedList<String>(),
			stop_s=new LinkedList<String>();
		int i=0;
		String cname,sname;//,location;
		for (Bundle b: context.getBundles()){
			String symname=b.getSymbolicName();
			if (symname.equals("selfstarmanager_asl")||symname.equals("flamenco_asl"))
				continue; 
			if (symname.equals("org.apache.felix.framework"))
				continue;
			if (symname==null || symname.length()==0)
				symname="noname"+i++;
			cname="c_"+symname;
			sname="s_"+symname;
/*			if (b.getLocation().startsWith("file:"))
				location=b.getLocation().substring(5);
			else*/
				//location=b.getLocation();
			init_c.add("init_component("+cname+", &SymbolicName="+symname+");\n");
			if (b.getState()==Bundle.ACTIVE){
				init_s.add("init_service(local,"+cname+","+sname+");\n");
				stop_s.add("stop_service("+sname+");\n");
			}
			undepl_c.add("undeploy_component("+cname+");\n");
			
		}
		LinkedList<String> script=init_c;
		script.add(0, "init_device(local);\n");
		script.add(0, "/*\n * ASL Script generated from a running configuration. \n * Please verify that the services are started in the right order \n */\n");
		
		script.addAll(init_s);
		script.addAll(stop_s);
		script.addAll(undepl_c);
		StringBuffer b = new StringBuffer();
		for (String op:script)
			b.append(op);
		text.setText(b.toString());
	}
/*	private StringBuilder generateInverseScript(){
		StringBuilder inverse= new StringBuilder(200);
		String script=text.getText();
		
		return inverse;
	}
*/
	private void setFrameIcon(JFrame frame){
/*		if (JarResources.isRunFromJar()){
			System.out.println("HERE!");
			JarResources jar = new JarResources("privacydemo.jar");
			byte[] b = jar.getResource("interactive/Diamond.gif");
			System.out.println(b.length);
			frame.setIconImage(new ImageIcon(b).getImage());
		} else {*/
			frame.setIconImage(new ImageIcon("asl\\interactive\\Diamond.gif").getImage());
	}

	private void evaluate(){
		boolean time = timeExecutionCheckBoxMenuItem.getState();
		if (time){
			long t2,t1=System.currentTimeMillis();
			interpreter.executeScript(text.getText());
			t2=System.currentTimeMillis();
			System.out.println("Execution timing: "+(t2-t1) +"ms");
			text.append("// "+(t2-t1) +" \n");
		} else
			interpreter.executeScript(text.getText());
	}
	/*
	private void makeButton(JPanel panel, String label, String actioncommand){
		JButton button = new JButton(label);
		button.addActionListener(this);
		panel.add(button);
	}*/
	
	/*public void actionPerformed(ActionEvent act) {
		System.out.println(act.getActionCommand());
		if ("show".equals(act.getActionCommand())){
			Bundle[] bl = context.getBundles();
			Bundle b;
			for (int i=0;i<bl.length;i++){
				b=bl[i];
				println(b.getBundleId()+"\t"+b.getState()+"\t"+b.getSymbolicName());
			}
		}
		
	}*/

	private static String indent="";

	public void print(String s){
			text.append(s);
			
	}
	
	public void println(String s){
		print(indent+s+"\n");
	}

}
