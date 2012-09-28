package dsk.codeview.plugin.view;

import static dsk.codeview.plugin.Const.NAME;

import java.awt.Component;
import java.io.IOException;

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
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
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

public class CodeView implements IPluginExtraTabView, ProjectEventListener,
        IEntitySelectionListener {
    private static final Logger LOG = LoggerFactory.getLogger(CodeView.class);

    private RSyntaxTextArea viewSource = new RSyntaxTextArea();

    private ClassExport export;
    private SkeletonCodeTools codeTools;
    private IDiagramViewManager diagramViewManager;

    public CodeView() {
        LOG.debug("CodeView.CodeView()");
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            this.diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (InvalidUsingException e) {
            LOG.error(e.getMessage(), e);
        }
        this.diagramViewManager.addEntitySelectionListener(this);

        this.viewSource.setEditable(false);
        this.viewSource.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
        this.viewSource.setCodeFoldingEnabled(true);
        this.viewSource.setAntiAliasingEnabled(true);

        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CodeViewModule());
        this.export = injector.getInstance(ClassExport.class);
        this.codeTools = injector.getInstance(SkeletonCodeTools.class);
    }

    /* IEntitySelectionListener */

    @Override
    public void entitySelectionChanged(IEntitySelectionEvent event) {
        LOG.debug("entitySelectionChanged");
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
            this.viewSource.setText(str.replace("\t", "  "));
            this.viewSource.setCaretPosition(0);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /* ProjectEventListener */

    @Override
    public void projectChanged(ProjectEvent event) {
        LOG.debug("CodeView.projectChanged()");
    }

    @Override
    public void projectClosed(ProjectEvent event) {
        LOG.debug("CodeView.projectClosed()");
    }

    @Override
    public void projectOpened(ProjectEvent event) {
        LOG.debug("CodeView.projectOpened()");
    }

    /* IPluginExtraTabView */

    @Override
    public void activated() {
        LOG.debug("CodeView.activated()");
    }

    @Override
    public void addSelectionListener(ISelectionListener listener) {
        LOG.debug("CodeView.addSelectionListener()");
    }

    @Override
    public void deactivated() {
        LOG.debug("CodeView.deactivated()");
    }

    @Override
    public Component getComponent() {
        RTextScrollPane sp = new RTextScrollPane(this.viewSource);
        sp.setFoldIndicatorEnabled(true);
        return sp;
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
