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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.php_export.core.ExportPath.ChooseState;
import dsk.php_export.core.delegate.DataBind;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.utils.SkeletonCodeTools;

public class SelectPackagesDialog extends JDialog implements DataBind<List<IClass>>,
        DataSelect<List<IClass>> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesDialog.class);
    private static final long serialVersionUID = -8542261936219304033L;

    private JList<ModelCheckBox<IClass>> list;

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
            list.setCellRenderer(new ListCellRenderer<ModelCheckBox<IClass>>() {
                @Override
                public Component getListCellRendererComponent(
                        JList<? extends ModelCheckBox<IClass>> list, ModelCheckBox<IClass> value,
                        int index, boolean isSelected, boolean cellHasFocus) {
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
                        chooseState = ChooseState.OK;
                        close();
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
                        close();
                    }
                });
                getRootPane().setDefaultButton(cancelButton);
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
        DefaultListModel<ModelCheckBox<IClass>> listModel = new DefaultListModel<>();
        for (IClass clazz : dataBindObject) {
            ModelCheckBox<IClass> checkBox = new ModelCheckBox<IClass>();
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
        List<IClass> selectedClasses = new ArrayList<>();
        ListModel<ModelCheckBox<IClass>> model = list.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; ++i) {
            ModelCheckBox<IClass> checkBox = model.getElementAt(i);
            if (checkBox.isSelected()) {
                selectedClasses.add(checkBox.getObject());
            }
        }
        return selectedClasses;
    }
}
