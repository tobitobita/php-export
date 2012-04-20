package dsk.php_export.plugin.action;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAssociationClass;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IRequirement;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITestCase;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

import dsk.php_export.plugin.utils.SkeletonCodeTools;

public class PhpExportAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		try {
			ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			IModel model = projectAccessor.getProject();
			List<IPackage> packageList = getModelList(IPackage.class, model, new ArrayList<IPackage>());
			for (IPackage p : packageList) {
				System.out.println(p.getName());
			}
			List<IClass> classList = getModelList(IClass.class, model, new ArrayList<IClass>());
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(window.getParent())) {
				System.out.println(fileChooser.getSelectedFile().getCanonicalPath());
				String dirPath = fileChooser.getSelectedFile().getCanonicalPath();
				for (IClass clazz : classList) {
					if (!(clazz instanceof IUseCase || clazz instanceof ITestCase || clazz instanceof ISubsystem
							|| clazz instanceof IRequirement || clazz instanceof IAssociationClass)) {
						printSkeletonCode(dirPath, clazz);
					}
				}
			}
			JOptionPane.showMessageDialog(window.getParent(), "出力しました", "", JOptionPane.INFORMATION_MESSAGE);
		} catch (ProjectNotFoundException e) {
			String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnExpectedException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnExpectedException();
		}
		return null;
	}

	private SkeletonCodeTools tools = new SkeletonCodeTools();

	private void printSkeletonCode(String exportDirPath, IClass clazz) {
		try {
			VelocityContext context = new VelocityContext();
			context.put("tools", tools);
			context.put("clazz", clazz);

			StringWriter sw = new StringWriter();
			Template template = this.getTemplate(tools.getClassTypeString(clazz));
			template.merge(context, sw);
			// System.out.println(sw.toString());
			this.write(exportDirPath, tools.getNamespace(clazz).replace("\\", "/"), clazz.getName(), sw.toString());
			sw.flush();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (MethodInvocationException e) {
			e.printStackTrace();
		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}

	private void write(String outputDirPath, String thePackagePath, String className, String text) {
		String fullDirPath = String.format("%s/%s", outputDirPath, thePackagePath);
		File dir = new File(fullDirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filepath = String.format("%s/%s.php", fullDirPath, className);
		Writer writer = null;
		try {
			writer = new FileWriter(new File(filepath));
			writer.write(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Template interfaceTemplate;
	private Template classTemplate;

	private Template getTemplate(String type) {
		try {
			Properties p = new Properties();
			p.load(PhpExportAction.class.getClassLoader().getResourceAsStream("velocity.properties"));
			VelocityEngine velocity = new VelocityEngine();
			velocity.init(p);
			if ("interface".equals(type)) {
				if (null == this.interfaceTemplate) {
					this.interfaceTemplate = velocity.getTemplate("php_interface_template.vm");
				}
				return this.interfaceTemplate;
			} else {
				if (null == this.classTemplate) {
					this.classTemplate = velocity.getTemplate("php_class_template.vm");
				}
				return this.classTemplate;
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 指定パッケージ配下のパッケージを、再帰的に全て取得する。
	 * 
	 * @param thePackage
	 *            指定パッケージ
	 * @param packageList
	 *            パッケージ一覧を格納するリスト
	 * @return パッケージ一覧を格納したリスト
	 */
	private <T extends INamedElement> List<T> getModelList(Class<T> clazz, IPackage model, List<T> packageList) {
		INamedElement[] namedElements = model.getOwnedElements();
		for (INamedElement namedElement : namedElements) {
			// もっとよい方法あるはず。。
			try {
				T obj = clazz.cast(namedElement);
				packageList.add(obj);
			} catch (Exception e) {
			}
			if (namedElement instanceof IPackage) {
				getModelList(clazz, (IPackage) namedElement, packageList);
			}
		}
		return packageList;
	}
}
