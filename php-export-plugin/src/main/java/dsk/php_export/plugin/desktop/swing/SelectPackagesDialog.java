package dsk.php_export.plugin.desktop.swing;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.WHITE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.common.message.ChooseState;
import dsk.common.util.SystemTools;
import dsk.export.delegate.DataBind;
import dsk.export.delegate.DataSelect;
import dsk.export.tools.SkeletonCodeTools;

public class SelectPackagesDialog extends JDialog implements DataBind<List<IClass>>, DataSelect<List<IClass>> {
	private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesDialog.class);
	private static final long serialVersionUID = -8542261936219304033L;

	private JList list;

	private ChooseState chooseState = ChooseState.CANCEL;

	/**
	 * Create the dialog.
	 */
	public SelectPackagesDialog() {
		LOG.trace("SelectPackagesDialog");
		this.initialize();
	}

	public void initialize() {
		this.initUI();
	}

	private void initUI() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setModal(true);
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			list = new JList();
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					super.mouseReleased(event);
					JList theList = (JList) event.getSource();
					JCheckBox theCheckBox = (JCheckBox) theList.getSelectedValue();
					if (null != theCheckBox) {
						theCheckBox.setSelected(!theCheckBox.isSelected());
					}
					repaint();
				}
			});
			list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					// System.out.println(e);
				}
			});
			list.setCellRenderer(new ListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					JCheckBox checkbox = (JCheckBox) value;
					if (isSelected) {
						checkbox.setForeground(WHITE);
						checkbox.setBackground(BLUE);
					} else {
						checkbox.setForeground(BLACK);
						checkbox.setBackground(WHITE);
					}
					return checkbox;
				}

			});
			scrollPane.getViewport().setView(list);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent event) {
					chooseState = ChooseState.OK;
					close();
				}
			});
			this.getRootPane().setDefaultButton(okButton);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent event) {
					close();
				}
			});
			// TODO スマートに
			if (SystemTools.isMacOsX()) {
				buttonPane.add(cancelButton);
				buttonPane.add(okButton);
			} else {
				buttonPane.add(okButton);
				buttonPane.add(cancelButton);
			}
		}
	}

	private void close() {
		setVisible(false);
	}

	/* Data binging */
	private SkeletonCodeTools tools = new SkeletonCodeTools();

	private List<IClass> dataBindObject = new ArrayList<IClass>();

	@Override
	public DataBind<List<IClass>> setDataBindObject(List<IClass> dataBindObject) {
		this.dataBindObject = dataBindObject;
		return this;
	}

	@Override
	public void bind() {
		DefaultListModel listModel = new DefaultListModel();
		for (IClass clazz : dataBindObject) {
			JModelCheckBox<IClass> checkBox = new JModelCheckBox<IClass>();
			String namespace = tools.getNamespace(clazz).replace("\\", ".");
			StringBuilder sb = new StringBuilder(clazz.getName());
			if (!StringUtils.isEmpty(namespace)) {
				sb.insert(0, ".");
				sb.insert(0, namespace);
			}
			checkBox.setObject(clazz);
			checkBox.setText(sb.toString());
			checkBox.setOpaque(true);
			listModel.addElement(checkBox);
		}
		this.list.setModel(listModel);
	}

	/* select window */

	@Override
	public void setData(List<IClass> data) {
		this.setDataBindObject(data).bind();
	}

	@Override
	public ChooseState select() {
		chooseState = ChooseState.CANCEL;
		this.setVisible(true);
		return this.chooseState;
	}

	@Override
	public List<IClass> getSelectedData() {
		List<IClass> selectedClasses = new ArrayList<IClass>();
		ListModel model = list.getModel();
		int size = model.getSize();
		for (int i = 0; i < size; ++i) {
			@SuppressWarnings("unchecked")
			JModelCheckBox<IClass> checkBox = (JModelCheckBox<IClass>) model.getElementAt(i);
			if (checkBox.isSelected()) {
				selectedClasses.add(checkBox.getObject());
			}
		}
		return selectedClasses;
	}
}
