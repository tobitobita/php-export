package dsk.codeview.plugin.view;

import static dsk.codeview.plugin.Const.NAME;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IEntitySelectionEvent;
import com.change_vision.jude.api.inf.view.IEntitySelectionListener;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import dsk.codeview.plugin.module.CodeViewModule;
import dsk.common.util.R;
import dsk.export.ClassExport;
import dsk.export.tools.SkeletonCodeTools;

public class CodeView implements IPluginExtraTabView, IEntitySelectionListener {
    private static final Logger LOG = LoggerFactory.getLogger(CodeView.class);

    private RSyntaxTextArea sourceView = new RSyntaxTextArea();

    private ClassExport export;
    private SkeletonCodeTools codeTools;
    private IDiagramViewManager diagramViewManager;

    public CodeView() {
        LOG.trace("CodeView create.");
        // インスタンス設定
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            this.diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (InvalidUsingException e) {
            LOG.error(e.getMessage(), e);
        }
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CodeViewModule());
        this.export = injector.getInstance(ClassExport.class);
        this.codeTools = injector.getInstance(SkeletonCodeTools.class);
        // 選択リスナに登録
        this.diagramViewManager.addEntitySelectionListener(this);
        // ソース表示の設定
        this.sourceView.setEditable(false);
        this.sourceView.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
        this.sourceView.setCodeFoldingEnabled(false);
        this.sourceView.setAntiAliasingEnabled(true);
    }

    /* IEntitySelectionListener */

    @Override
    public void entitySelectionChanged(IEntitySelectionEvent event) {
        IPresentation[] presentations = this.diagramViewManager.getSelectedPresentations();
        if (0 >= presentations.length) {
            return;
        }
        IElement model = presentations[0].getModel();
        if (!this.codeTools.isClass(model)) {
            return;
        }
        IClass clazz = (IClass) model;
        if (StringUtils.isEmpty(clazz.getName())) {
            return;
        }

        try {
            String str = this.export.createSkeletonCode(clazz);
            this.sourceView.setText(str.replace("\t", "  "));
            this.sourceView.setCaretPosition(0);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (Error e) {
            LOG.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Code View Pluginは、6.6.3以下のバージョンには対応していません", "Alert",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* IPluginExtraTabView */

    @Override
    public void activated() {
    }

    @Override
    public void addSelectionListener(ISelectionListener listener) {
    }

    @Override
    public void deactivated() {
    }

    @Override
    public Component getComponent() {
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        // バー
        JPanel barPane = new JPanel(new BorderLayout(0, 0));
        contentPane.add(barPane, BorderLayout.NORTH);
        JToolBar toolBar = new JToolBar();
        barPane.add(toolBar, BorderLayout.EAST);
        toolBar.setAlignmentY(Component.CENTER_ALIGNMENT);
        toolBar.setFloatable(false);
        JComboBox comboBox = new JComboBox(this.selectedKind());
        comboBox.setEditable(false);
        comboBox.setPrototypeDisplayValue("Javascript");
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 出力選択変更
                // JComboBox comboBox = (JComboBox) e.getSource();
                // System.out.println(comboBox.getSelectedItem().toString());
            }
        });
        toolBar.add(comboBox);
        // 表示エリア
        RTextScrollPane sp = new RTextScrollPane(this.sourceView);
        sp.setFoldIndicatorEnabled(true);
        contentPane.add(sp);
        return contentPane;
    }

    /**
     * TODO 出力の種類
     */
    private String[] selectedKind() {
        return new String[] { "PHP" };
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getTitle() {
        return R.m(NAME, "Code　View");
    }
}
