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
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/

package eu.linksmart.security;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import eu.linksmart.security.protocols.net.Consts;
import eu.linksmart.security.protocols.net.common.MissingSenderCertificateException;
import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
import eu.linksmart.security.protocols.net.transport.Command;



/**
 * SecureSessionGui
 */
public class SecureSessionGui extends javax.swing.JFrame {

	private JPanel jPanel1;
	private JButton buttonRequestInfo;
	private JTextField textReceiver;
	private JButton buttonInviteDomain;
	private JButton buttonGetKey;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JTextField textSource;

	/**
	 * Constructor
	 */
	public SecureSessionGui() {
		super();
		initGUI();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Main method to display this JFrame
	 * 
	 * @param args the arguments received
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SecureSessionGui inst = new SecureSessionGui();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	/**
	 * Initializes the GUI
	 */
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			{
				jPanel1 = new JPanel();
				GridBagLayout jPanel1Layout = new GridBagLayout();
				jPanel1Layout.columnWidths = new int[] {130, 10, 7};
				jPanel1Layout.rowHeights = new int[] {7, 7, 7};
				jPanel1Layout.columnWeights = new double[] {0.0, 0.0, 0.1};
				jPanel1Layout.rowWeights = new double[] {0.1, 0.1, 0.1};
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setLayout(jPanel1Layout);
				jPanel1.setPreferredSize(new java.awt.Dimension(681, 300));
				
				{
					textReceiver = new JTextField();
					jPanel1.add(textReceiver, new 
						GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
							new Insets(0, 0, 0, 0), 0, 0));
					textReceiver.setText("11.12.13.14");
				}
				
				{
					buttonRequestInfo = new JButton();
					jPanel1.add(buttonRequestInfo, new 
						GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
							GridBagConstraints.CENTER, GridBagConstraints.NONE, 
							new Insets(0, 0, 0, 0), 0, 0));
					buttonRequestInfo.setText("Get Info");
					buttonRequestInfo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							Command command = new Command(Command.CLIENT_REQUESTS_INFO);
							command.put("server", textReceiver.getText());
							command.put("client", textSource.getText());
							SecureSessionControllerImpl.getInstance().
								handleCommand(command, Consts.CLIENT);
						}
					});
				}
				
				{
					textSource = new JTextField();
					jPanel1.add(textSource, new 
						GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 
							GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
							new Insets(0, 0, 0, 0), 0, 0));
					textSource.setText("11.12.13.15");
				}
				
				{
					jLabel1 = new JLabel();
					jPanel1.add(jLabel1, new 
						GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					jLabel1.setText("Source:");
				}
				
				{
					jLabel2 = new JLabel();
					jPanel1.add(jLabel2, new 
						GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
							GridBagConstraints.CENTER, GridBagConstraints.NONE, 
							new Insets(0, 0, 0, 0), 0, 0));
					jLabel2.setText("Target:");
				}
				
				{
					buttonGetKey = new JButton();
					jPanel1.add(buttonGetKey, new 
						GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE, 
							new Insets(0, 0, 0, 0), 0, 0));
					buttonGetKey.setText("Exchange certificates");
					buttonGetKey.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							/*
							 * This is a synchronous call (returns only when 
							 * protocol has finished
							 */
							try {
								String certReference = SecureSessionControllerImpl.
									getInstance().getCertificateIdentifier(
										textSource.getText(), textReceiver.getText());
								System.out.println("Got a certificate reference: "
									+ certReference);
							} catch (MissingSenderCertificateException e) {
								e.printStackTrace();
							}
						}
					});
				}
				
				{
					buttonInviteDomain = new JButton();
					jPanel1.add(buttonInviteDomain, new 
						GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE, 
							new Insets(0, 0, 0, 0), 0, 0));
					buttonInviteDomain.setText("Invite HID to Domain");
					buttonInviteDomain.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							Command command = new Command(Command.CLIENT_INVITE_DOMAIN);
							command.put("server", textReceiver.getText());
							command.put("client", textSource.getText());
							System.out.println("STARTING");
							try {
								SecureSessionControllerImpl.
									getInstance().getCertificateIdentifier(
										textSource.getText(), textReceiver.getText());
							} catch (MissingSenderCertificateException e) {
								e.printStackTrace();
							}
							System.out.println("FINISHED");
						}
					});
				}
			}
			
			pack();
			this.setSize(700, 400);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
