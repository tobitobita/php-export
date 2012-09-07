package dsk.php_export.core.desktop.swing;

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

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.php_export.core.delegate.DataBind;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.utils.SkeletonCodeTools;

public class SelectPackagesDialog extends JDialog implements DataBind<List<IClass>>,
        DataSelect<List<IClass>> {
    private static final long serialVersionUID = 1L;

    private JList<JCheckBox> list;

    /**
     * Create the dialog.
     */
    public SelectPackagesDialog() {
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
            list = new JList<>();
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent event) {
                    super.mouseReleased(event);
                    @SuppressWarnings("unchecked")
                    JList<JCheckBox> theList = (JList<JCheckBox>) event.getSource();
                    JCheckBox theCheckBox = (JCheckBox) theList.getSelectedValue();
                    if (null != theCheckBox) {
                        theCheckBox.setSelected(!theCheckBox.isSelected());
                    }
                    repaint();
                }
            });
            list.setCellRenderer(new ListCellRenderer<JCheckBox>() {
                @Override
                public Component getListCellRendererComponent(JList<? extends JCheckBox> list,
                        JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (isSelected) {
                        value.setForeground(WHITE);
                        value.setBackground(BLUE);
                    } else {
                        value.setForeground(BLACK);
                        value.setBackground(WHITE);
                    }
                    return value;
                }

            });
            scrollPane.getViewport().setView(list);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        ListModel<JCheckBox> model = list.getModel();
                        int size = model.getSize();
                        System.out.println();
                        for (int i = 0; i < size; ++i) {
                            JCheckBox checkBox = model.getElementAt(i);
                            if (checkBox.isSelected()) {
                                System.out.println(checkBox.getText());
                            }
                        }
                    }
                });
                buttonPane.add(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        setVisible(false);
                        dispose();
                    }
                });
                getRootPane().setDefaultButton(cancelButton);
                buttonPane.add(cancelButton);
            }
        }
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
        DefaultListModel<JCheckBox> listModel = new DefaultListModel<>();
        for (IClass clazz : dataBindObject) {
            JCheckBox checkBox = new JCheckBox();
            String namespace = tools.getNamespace(clazz).replace("\\", ".");
            StringBuilder sb = new StringBuilder(clazz.getName());
            if (!StringUtils.isEmpty(namespace)) {
                sb.insert(0, ".");
                sb.insert(0, namespace);
            }
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
    public void select() {
        this.setVisible(true);
    }

    @Override
    public List<IClass> getSelectedData() {
        // TODO Auto-generated method stub
        return null;
    }
}
