package dsk.php_export.plugin.desktop.swing;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dsk.common.message.ChooseState;
import dsk.common.message.Message;
import dsk.common.util.R;

public class CheckBoxMessage implements Message<Boolean> {

	private Component parent;

	private JCheckBox checkBox;

	public void setParent(Component parent) {
		this.parent = parent;
	}

	@Override
	public ChooseState showMessage(String message) {
		this.checkBox = new JCheckBox(R.m("以降のファイルもすべて上書きする"));
		String displayStr = String.format("\"%s\"\n%s", message, R.m("このファイルはすでに存在します。上書きしますか？"));
		int result = JOptionPane.showOptionDialog(this.parent, new Object[] { displayStr, checkBox }, "",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
		if (JOptionPane.OK_OPTION == result) {
			return ChooseState.OK;
		}
		return ChooseState.CANCEL;
	}

	@Override
	public Boolean getValue() {
		if (this.checkBox == null) {
			return false;
		}
		return this.checkBox.isSelected();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				Message<Boolean> m = new CheckBoxMessage();
				System.out.println(m.showMessage("asdf\nasdf\nqweqeqr"));
				System.out.println(m.getValue());
				f.dispose();
			}
		});
	}
}
