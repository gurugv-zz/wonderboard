import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.gurugv.wonderboard.client.Constants;
import com.gurugv.wonderboard.client.Util;

public class WbClient {

	private static final int MAX_MENULABEL_LENGTH = 15;
	private String userId;
	private String userPassword;
	private TrayIcon trayIcon;
	private MenuItem userIdMenuItem;
	private Menu shareItemsMenu;
	private HashMap<String, String> currentSharedItemsMap = new HashMap<String, String>();

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
		MenuItem publishItem = new MenuItem("Publish Clipboard");
		MenuItem refresh = new MenuItem("Refresh Clipboard From Network");
		MenuItem shareMenuItem = new MenuItem("Share Clipboard ...");
		shareItemsMenu = new Menu("Shared Items ");
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

		// popup.add(cb1);
		// popup.add(cb2);
		popup.addSeparator();
		popup.add(shareMenuItem);
		popup.add(shareItemsMenu);
		popup.addSeparator();
		popup.add(publishItem);
		popup.add(refresh);
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
		shareMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String shareTouserId = JOptionPane
						.showInputDialog("Enter userId to share with: ");
				if (shareTouserId == null || shareTouserId.isEmpty()) {
					return;
				}

				try {

					String result = Util.shareCurrentClipboardTo(userId,
							userPassword, shareTouserId);
					if (result != null && result.startsWith("Success:")) {
						trayIcon.displayMessage("Successfully Shared!", result,
								MessageType.INFO);
					} else {
						trayIcon.displayMessage("Share Failed !", result,
								MessageType.ERROR);
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
					trayIcon.displayMessage("Share Failed !", e1.getMessage(),
							MessageType.ERROR);
				}
			}
		});

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
		MenuShortcut refreshShortCut = new MenuShortcut(KeyEvent.VK_C);
		refresh.setShortcut(refreshShortCut);
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
					HashMap<String, String> sharedItems = Util.refreshShared(
							userId, userPassword);
					boolean anyNewSharedContent = checkIfAnyNewContent(sharedItems);
					updateShareItemsUI(sharedItems);
					String resultToDispaly = anyNewSharedContent ? "New Shared Item(s) Received! \n"
							: "";
					resultToDispaly += text.equals(Constants.NOT_AVIALABLE)? "No Local content." : ("ClipBoard Updated : "+text);
					System.out.println("sharedItems = " + sharedItems);
					trayIcon.displayMessage("Refresh Succesful", resultToDispaly,
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

	private void updateShareItemsUI(HashMap<String, String> sharedItems) {
		this.currentSharedItemsMap = sharedItems;
		shareItemsMenu.removeAll();
		Iterator<Entry<String, String>> itrator = sharedItems.entrySet()
				.iterator();
		if (currentSharedItemsMap.isEmpty()) {
			shareItemsMenu.add(new MenuItem("NONE"));
		}
		while (itrator.hasNext()) {
			Entry<String, String> entry = itrator.next();
			String fromUserName = entry.getKey();
			final String fromUserData = entry.getValue();
			Menu userMenuItem = new Menu(fromUserName);
			shareItemsMenu.add(userMenuItem);
			String fromUserDataLabel = fromUserData.length() < MAX_MENULABEL_LENGTH ? fromUserData
					: (fromUserData.substring(0, MAX_MENULABEL_LENGTH - 3) + "...");
			MenuItem dataMenuItem = new MenuItem(fromUserDataLabel);
			userMenuItem.add(dataMenuItem);
			userMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.updateClipboard(fromUserData);
				}
			});
			dataMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.updateClipboard(fromUserData);
				}
			});

		}
	}

	private boolean checkIfAnyNewContent(HashMap<String, String> sharedItems) {
		if (this.currentSharedItemsMap.size() < sharedItems.size()) {
			return true;
		}
		Iterator<Entry<String, String>> newSharedItr = sharedItems.entrySet()
				.iterator();
		while (newSharedItr.hasNext()) {
			Entry<String, String> tmp = newSharedItr.next();
			if (currentSharedItemsMap.get(tmp.getKey()) == null) {
				return true;
			}
			if (!currentSharedItemsMap.get(tmp.getKey()).equals(tmp.getValue())) {
				return true;
			}
		}
		return false;
	}

	void login() {
		loginWorkflow();
		userIdMenuItem.setLabel(userId == null ? "<<LOGIN>>" : "Logged in: "
				+ userId);
	}

	void loginWorkflow() {
		String tmpUser = getUserIdInput();
		if (tmpUser == null) {
			return;
		}
		String tmpUserPass = getUserPass(tmpUser);
		if (tmpUserPass == null) {
			return;
		}
		boolean valid = false;
		try {
			valid = Util.authenticate(tmpUser, tmpUserPass);
			if (valid) {
				userId = tmpUser;
				userPassword = tmpUserPass;
				trayIcon.displayMessage("Welcome", "Welcome " + userId,
						MessageType.INFO);
			} else {
				userId = null;
				trayIcon.displayMessage("Authentication Failed",
						"Login Failed!", MessageType.ERROR);

			}
		} catch (IOException e) {
			e.printStackTrace();
			userId = null;
			trayIcon.displayMessage("Not avaialble",
					"unable to connect " + e.getMessage(), MessageType.ERROR);
		}
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
