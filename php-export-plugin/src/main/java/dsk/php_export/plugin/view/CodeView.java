package dsk.php_export.plugin.view;

import static dsk.php_export.plugin.Const.NAME;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAssociationClass;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IRequirement;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITestCase;
import com.change_vision.jude.api.inf.model.IUseCase;
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

import dsk.common.util.R;
import dsk.php_export.core.PhpExport;
import dsk.php_export.plugin.module.CodeViewModule;

public class CodeView implements IPluginExtraTabView, ProjectEventListener,
        IEntitySelectionListener {
    private static final Logger LOG = LoggerFactory.getLogger(CodeView.class);

    private JTextArea viewSource = new JTextArea();

    private PhpExport export;

    public CodeView() {
        LOG.debug("CodeView.CodeView()");
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            IDiagramViewManager diagramViewManager = projectAccessor.getViewManager()
                    .getDiagramViewManager();
            diagramViewManager.addEntitySelectionListener(this);
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (InvalidUsingException e) {
            LOG.error(e.getMessage(), e);
        }
        this.viewSource.setEditable(false);

        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CodeViewModule());
        this.export = injector.getInstance(PhpExport.class);
    }

    /* IEntitySelectionListener */

    @Override
    public void entitySelectionChanged(IEntitySelectionEvent event) {
        LOG.debug("entitySelectionChanged");
        IPresentation[] presentations = this.getSelectingPresentation();
        String name = null;
        // Class以外を除外
        if (0 < presentations.length) {
            IElement model = presentations[0].getModel();
            if (model instanceof IClass
                    && !(model instanceof IUseCase || model instanceof ITestCase
                            || model instanceof ISubsystem || model instanceof IRequirement || model instanceof IAssociationClass)) {
                name = ((IClass) model).getName();
            }
            if (!StringUtils.isEmpty(name)) {
                try {
                    this.viewSource.setText(this.export.createSkeletonCode((IClass) model));
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
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
        return new JScrollPane(this.viewSource);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getTitle() {
        return R.m(NAME, "Code　View");
    }

    private IPresentation[] getSelectingPresentation() {
        IPresentation[] presentations = new IPresentation[0];
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            presentations = projectAccessor.getViewManager().getDiagramViewManager()
                    .getSelectedPresentations();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return presentations;
    }
}
