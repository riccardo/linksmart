/**
 * Copyright (C) 2005-2010 Mads Ingstrup
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * The license is available from <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 2005-04-08
 * modified on 2010-11-11
 * @author Mads Ingstrup
 */
package eu.linksmart.selfstar.aql.interactive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import eu.linksmart.selfstar.aql.AQLService;
import eu.linksmart.selfstar.aql.db.QueryTree;
import eu.linksmart.selfstar.aql.distribution.QueryManager;


/**
 * @author ingstrup
 * 
 * 
 */
public class AQLShell {

	JFrame motherframe, xadlWin;
	JTextArea queryfield;
	JTextField archfilefield;
	JTable resulttable;
	JMenuBar topmenu;
	JMenu file;
	JButton evaluate;
//	Architecture theArch=null;
	String defaultPath = "\\home\\ingstrup\\workspace2\\AQL\\awacsout.xml"; //C:\\d\\dwl\\eclipse\\eclipse-SDK-3.1M4-win32\\eclipse\\workspace\\AQL\\";
	
	static int selectionCount=0;
	
	public AQLShell(){
		makeMotherframe();
		//changeArchitecture();
	}
	
	private void makeMotherframe(){
		//JFrame.setDefaultLookAndFeelDecorated(true);
		motherframe = new JFrame("Interactive AQL Processor");
		motherframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		motherframe.setContentPane(createContentPane());
		
		//set icon:
		//motherframe.setIconImage(new ImageIcon("interactive\\Diamond.gif").getImage());
		//setFrameIcon(motherframe);
		
		motherframe.pack();
		motherframe.resize(400,300);
		motherframe.move(400,200);
		motherframe.setVisible(true);
		
	}
	
	public JPanel createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        //Create a scrolled text area.
        queryfield = new JTextArea();
        queryfield.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(queryfield);
        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);
        evaluate = new JButton("Evaluate");
        evaluate.setToolTipText("Evaluate the current text as a query and display result in a table");
        evaluate.addActionListener(new ActionListener(){ 
        	public void actionPerformed(ActionEvent arg0) {
				evaluate();
			}
        });
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(evaluate, BorderLayout.SOUTH);

        /*JPanel top = new JPanel(new BorderLayout(5,5));
        top.add(new JLabel(" Architecture: "),BorderLayout.WEST);
        archfilefield = new JTextField(20);
        archfilefield.setToolTipText("Use \"change\" button to change this file");
        archfilefield.setEditable(false);
       
                
        setDefaultPath();
        	
        top.add(archfilefield, BorderLayout.CENTER);
        JButton changearch = new JButton("change");
        changearch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				changeArchitecture();
			}
        });
        top.add(changearch, BorderLayout.EAST);
        contentPane.add(top,BorderLayout.NORTH);
        */
        return contentPane;
    }

	private AQLService aqls;
	public void setAQL(AQLService aql){
		this.aqls=aql;
	}
	
	private void evaluate(){
		String s = queryfield.getText();
		boolean distributed=false;
		try {
			if (aqls==null){
				JOptionPane.showMessageDialog(motherframe, "The AQL bundle is not active:\nUnable to process query");
				return;
			}
			if (distributed)
				QueryManager.getInstance().doDistributedQuery(QueryTree.parseQuery(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

/*	private void setFrameIcon(JFrame frame){
		if (JarResources.isRunFromJar()){
			System.out.println("HERE!");
			JarResources jar = new JarResources("privacydemo.jar");
			byte[] b = jar.getResource("interactive/Diamond.gif");
			System.out.println(b.length);
			frame.setIconImage(new ImageIcon(b).getImage());
		} else {
			frame.setIconImage(new ImageIcon("interactive\\Diamond.gif").getImage());
		}
		
	}
	*/
	public void showSelection(Vector rows, Vector columnNames){
		JFrame frame = new JFrame("Selection "+selectionCount++);
		//setFrameIcon(frame);
		JPanel panel = new JPanel(new BorderLayout());
		JTable table = new JTable(rows, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(100+100*columnNames.size(), 100+20*rows.size()));
		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll, BorderLayout.CENTER);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.move(600+5*selectionCount,70+5*selectionCount);
		frame.setVisible(true);
		
	}
	
	/*private void changeArchitecture(){
		String path = archfilefield.getText();
		if (path.length()>3)
			path = path.substring(0,path.lastIndexOf("\\"));
		else
			path=null;
		JFileChooser fc = new JFileChooser(path);
		int rval = fc.showOpenDialog(motherframe);
		if (rval== JFileChooser.APPROVE_OPTION){
			File f = fc.getSelectedFile();
			try {
				XADL_Importer imp = new XADL_Importer(f);
				theArch = imp.getArchitecture();
				archfilefield.setText(f.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

	public void setDefaultPath(){
		if (new File(defaultPath).exists())
			archfilefield.setText(defaultPath);
	}
	
	public static void main(String args[]){
		new AQLShell();
	}
}
