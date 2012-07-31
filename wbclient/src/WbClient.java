import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.gurugv.wonderboard.client.Util;

public class WbClient {

	private String userId;
	private String userPassword;
	private TrayIcon trayIcon;
	private MenuItem userIdMenuItem;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// String userId = args.length > 0 ? args[0] : "ggv";
		WbClient c = new WbClient();
		c.createAndShowGUI();
		System.out.println("showing");

	}

	private void createAndShowGUI() throws IOException {

		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("images/wb.gif", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		userIdMenuItem = new MenuItem(userId);
		MenuItem publishItem = new MenuItem("Publish");
		MenuItem refresh = new MenuItem("Refresh From Network");
		// CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
		// CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
		// Menu displayMenu = new Menu("Display");
		// MenuItem errorItem = new MenuItem("Error");
		// MenuItem warningItem = new MenuItem("Warning");
		// MenuItem infoItem = new MenuItem("Info");
		// MenuItem noneItem = new MenuItem("None");
		MenuItem exitItem = new MenuItem("Exit");

		// Add components to popup menu
		popup.add(userIdMenuItem);
		popup.addSeparator();
		popup.add(publishItem);
		popup.add(refresh);
		// popup.add(cb1);
		// popup.add(cb2);
		popup.addSeparator();
		// popup.add(displayMenu);
		// displayMenu.add(errorItem);
		// displayMenu.add(warningItem);
		// displayMenu.add(infoItem);
		// displayMenu.add(noneItem);
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}
		publishItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (userId == null) {
						trayIcon.displayMessage("Login required",
								"click on the username for login",
								MessageType.ERROR);
						return;
					}
					String data = Util.publish(userId, userPassword);
					trayIcon.displayMessage("Publish Successful", data,
							MessageType.INFO);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					trayIcon.displayMessage("Publish ERROR", "error occured  "
							+ e1.getMessage(), MessageType.ERROR);
				}
			}
		});

		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (userId == null) {
						trayIcon.displayMessage("Login required",
								"click on the username for login",
								MessageType.ERROR);
						return;
					}
					String text = Util.refresh(userId, userPassword);
					trayIcon.displayMessage("Refresh Succesful", text,
							MessageType.INFO);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					trayIcon.displayMessage("Refresh ERROR", "error occured  "
							+ e1.getMessage(), MessageType.ERROR);
				}
			}
		});

		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null,
				// "This dialog box is run from System Tray");
			}
		});

		userIdMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();

				// JOptionPane.showMessageDialog(null,
				// "This dialog box is run from the About menu item");
			}
		});

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});
		login();
	}

	void login() {
		String tmpUser = getUserIdInput();
		String tmpUserPass = getUserPass(tmpUser);
		boolean valid = false;
		try {
			valid = Util.authenticate(tmpUser, tmpUserPass);
		} catch (IOException e) {
			e.printStackTrace();
			userId = null;
			trayIcon.displayMessage("Not avaialble", "unable to connect",
					MessageType.ERROR);
		}
		if (valid) {
			userId = tmpUser;
			userPassword = tmpUserPass;
			trayIcon.displayMessage("Welcome", "Welcome " + userId,
					MessageType.INFO);
		} else {
			userId = null;
			trayIcon.displayMessage("Authentication Failed", "Login Failed!",
					MessageType.ERROR);

		}
		userIdMenuItem.setLabel(userId == null ? "<<LOGIN>>" : userId);
	}

	private String getUserPass(String tmpUser) {
		String input = JOptionPane.showInputDialog("Input password for ["
				+ tmpUser + "] : ");
		return input;
	}

	static String getUserIdInput() {
		return JOptionPane.showInputDialog("Input userid ");
	}

	// Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = WbClient.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

}
