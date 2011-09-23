/**
 * Copyright (C) 2002-2010 Mads Ingstrup
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
 * The GNU Lesser General Public License version 3 is 
 * available from <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.selfstar.aql.interactive;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class builds a menu from a handler class based on annotations of the methods that should be represented by the menu.
 * Exampl: @AutoMenuItem(menuName="View",itemName="Reduce crossings", shortCutKey=KeyEvent.VK_F1)
	
 * @author ingstrup
 *
 */
public class AutoMenu {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface AutoMenuItem {
		public String menuName();
		public String itemName();
		public int shortCutKey() default -1;
	}
	
	
	public static JMenuBar makeMenu(final Object handler) {
		
		Hashtable<String,JMenu> menus=new Hashtable<String,JMenu>(){
			@Override
			public JMenu get(Object key){
				if (!containsKey(key))
					put((String)key,new JMenu((String) key));
				return super.get(key);
			}
		};
		JMenuItem menuitem;
		for (Method m : handler.getClass().getMethods()){
			if(m.isAnnotationPresent(AutoMenuItem.class)){
				AutoMenuItem item = m.getAnnotation(AutoMenuItem.class);
				//System.out.println("Processing menu item from "+handler+" menu: "+item.menuName()+"name: "+item.itemName());
				menuitem=new JMenuItem(item.itemName());
				final Method method=m;
				menuitem.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						try {
							method.invoke(handler, (Object[])null);
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}
				});
				//System.out.println(menus+" "+menus.get(item.menuName()));
				menus.get(item.menuName()).add(menuitem);
				if (item.shortCutKey()!=-1)
					menuitem.setAccelerator(KeyStroke.getKeyStroke(item.shortCutKey(),0));
			}
		}
/*		for (Field f: handler.getClass().getFields()){
			
		}
	*/	JMenuBar menubar = new JMenuBar();
		//setColors(menubar);
		for (JMenu m:menus.values()){
			menubar.add(m);
			//setColors(m);
		}
		return menubar;
	}

	static void setColors(Component c){
		
		//c.setBackground(Color.DARK_GRAY);
		//c.setForeground(Color.white);
	}
	
}
