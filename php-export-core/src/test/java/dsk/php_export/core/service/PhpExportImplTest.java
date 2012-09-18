package dsk.php_export.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IHyperlink;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPort;
import com.change_vision.jude.api.inf.model.IRealization;
import com.change_vision.jude.api.inf.model.ITaggedValue;
import com.change_vision.jude.api.inf.model.ITemplateBinding;
import com.change_vision.jude.api.inf.model.IUsage;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

import dsk.php_export.core.ExportPath;
import dsk.php_export.core.ExportPath.ChooseState;
import dsk.php_export.core.PhpExport;
import dsk.php_export.core.PhpExport.ExportState;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.exception.ExportException;

public class PhpExportImplTest {
    private ProjectAccessor pa;

    @Before
    public void before() {
        System.out.println("PhpExportImplTest.before()");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("php_test.asta")) {
            pa = ProjectAccessorFactory.getProjectAccessor();
            pa.open(is);
        } catch (LicenseNotFoundException | ProjectNotFoundException | NonCompatibleException
                | IOException | ProjectLockedException | ClassNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void after() {
        System.out.println("PhpExportImplTest.after()");
        if (null != pa) {
            pa.close();
        }
    }

    @Test
    public void 選択したクラスが0個() {
        PhpExport export = new PhpExportService(new ExportPath() {
            @Override
            public ChooseState choose() {
                return ChooseState.OK;
            }

            @Override
            public String getChoosePath() {
                return "/Users/makoto/Documents/github/php-export/php-export-core/target/export/";
            }
        }, new DataSelect<List<IClass>>() {
            @Override
            public void setData(List<IClass> data) {
            }

            @Override
            public ChooseState select() {
                return ChooseState.OK;
            }

            @Override
            public List<IClass> getSelectedData() {
                List<IClass> classes = new ArrayList<>();
                return classes;
            }
        });
        try {
            Assert.assertEquals(ExportState.ES_FAILD, export.export(pa));
        } catch (ProjectNotFoundException | IOException | ExportException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void 選択したクラスが1個() {
        PhpExport export = new PhpExportService(new ExportPath() {
            @Override
            public ChooseState choose() {
                return ChooseState.OK;
            }

            @Override
            public String getChoosePath() {
                return "/Users/makoto/Documents/github/php-export/php-export-core/target/export/";
            }
        }, new DataSelect<List<IClass>>() {
            @Override
            public void setData(List<IClass> data) {
            }

            @Override
            public ChooseState select() {
                return ChooseState.OK;
            }

            @Override
            public List<IClass> getSelectedData() {
                List<IClass> classes = new ArrayList<>();
                classes.add(new IClass() {
                    @Override
                    public IHyperlink[] getHyperlinks() {
                        return null;
                    }

                    @Override
                    public void deleteHyperlink(IHyperlink arg0) throws InvalidEditingException {
                    }

                    @Override
                    public IHyperlink createURLHyperlink(String arg0, String arg1)
                            throws InvalidEditingException {
                        return null;
                    }

                    @Override
                    public IHyperlink createFileHyperlink(String arg0, String arg1, String arg2)
                            throws InvalidEditingException {
                        return null;
                    }

                    @Override
                    public IHyperlink createElementHyperlink(IElement arg0, String arg1)
                            throws InvalidEditingException {
                        return null;
                    }

                    @Override
                    public void setTypeModifier(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void removeStereotype(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public boolean isReadOnly() {
                        return false;
                    }

                    @Override
                    public String getTypeModifier() {
                        return null;
                    }

                    @Override
                    public ITaggedValue[] getTaggedValues() {
                        return null;
                    }

                    @Override
                    public String getTaggedValue(String arg0) {
                        return null;
                    }

                    @Override
                    public String[] getStereotypes() {
                        return new String[0];
                    }

                    @Override
                    public IPresentation[] getPresentations() throws InvalidUsingException {
                        return null;
                    }

                    @Override
                    public IElement getOwner() {
                        return null;
                    }

                    @Override
                    public String getId() {
                        return null;
                    }

                    @Override
                    public IElement[] getContainers() {
                        return null;
                    }

                    @Override
                    public IElement getContainer() {
                        return null;
                    }

                    @Override
                    public IComment[] getComments() {
                        return null;
                    }

                    @Override
                    public void addStereotype(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setVisibility(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setName(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setDefinition(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setAlias2(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setAlias1(String arg0) throws InvalidEditingException {
                    }

                    @Override
                    public boolean isPublicVisibility() {
                        return false;
                    }

                    @Override
                    public boolean isProtectedVisibility() {
                        return false;
                    }

                    @Override
                    public boolean isPrivateVisibility() {
                        return false;
                    }

                    @Override
                    public boolean isPackageVisibility() {
                        return false;
                    }

                    @Override
                    public IUsage[] getSupplierUsages() {
                        return null;
                    }

                    @Override
                    public IRealization[] getSupplierRealizations() {
                        return null;
                    }

                    @Override
                    public IDependency[] getSupplierDependencies() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return "test";
                    }

                    @Override
                    public String getFullNamespace(String arg0) {
                        return null;
                    }

                    @Override
                    public String getFullName(String arg0) {
                        return null;
                    }

                    @Override
                    public IDiagram[] getDiagrams() {
                        return null;
                    }

                    @Override
                    public String getDefinition() {
                        return null;
                    }

                    @Override
                    public IConstraint[] getConstraints() {
                        return null;
                    }

                    @Override
                    public IUsage[] getClientUsages() {
                        return null;
                    }

                    @Override
                    public IRealization[] getClientRealizations() {
                        return null;
                    }

                    @Override
                    public IDependency[] getClientDependencies() {
                        return null;
                    }

                    @Override
                    public String getAlias2() {
                        return null;
                    }

                    @Override
                    public String getAlias1() {
                        return null;
                    }

                    @Override
                    public void setLeaf(boolean arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setActive(boolean arg0) throws InvalidEditingException {
                    }

                    @Override
                    public void setAbstract(boolean arg0) throws InvalidEditingException {
                    }

                    @Override
                    public boolean isPrimitiveType() {
                        return false;
                    }

                    @Override
                    public boolean isLeaf() {
                        return false;
                    }

                    @Override
                    public boolean isActive() {
                        return false;
                    }

                    @Override
                    public boolean isAbstract() {
                        return false;
                    }

                    @Override
                    public IClassifierTemplateParameter[] getTemplateParameters() {
                        return null;
                    }

                    @Override
                    public ITemplateBinding[] getTemplateBindings() {
                        return null;
                    }

                    @Override
                    public IGeneralization[] getSpecializations() {
                        return null;
                    }

                    @Override
                    public IPort[] getPorts() {
                        return null;
                    }

                    @Override
                    public IOperation[] getOperations() {
                        return null;
                    }

                    @Override
                    public IClass[] getNestedClasses() {
                        return null;
                    }

                    @Override
                    public IGeneralization[] getGeneralizations() {
                        return null;
                    }

                    @Override
                    public IAttribute[] getAttributes() {
                        return null;
                    }
                });
                return classes;
            }
        });
        try {
            Assert.assertEquals(ExportState.ES_SUCCESS, export.export(pa));
        } catch (ProjectNotFoundException | IOException | ExportException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
