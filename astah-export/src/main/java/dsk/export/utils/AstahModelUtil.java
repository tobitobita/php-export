package dsk.export.utils;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IAssociationClass;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IRequirement;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITestCase;
import com.change_vision.jude.api.inf.model.IUseCase;

/**
 * astah上のモデルを扱うユーティリティ
 * 
 * @author makoto
 * 
 */
public class AstahModelUtil {

	/**
	 * 名前空間をフルパスで取得する
	 * 
	 * @param clazz
	 *            クラス
	 * @return クラス名（フルパス）
	 */
	public String getNamespace(IClass clazz) {
		StringBuilder sb = new StringBuilder();
		IElement owner = clazz.getOwner();
		while (owner != null && owner instanceof INamedElement && owner.getOwner() != null) {
			sb.insert(0, String.format("%s/", ((INamedElement) owner).getName()));
			owner = owner.getOwner();
		}
		if (0 < sb.length()) {
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}

	/**
	 * プロジェクトルートより、すべてのパッケージを取得する
	 * 
	 * @param rootModel
	 *            ルートモデル
	 * @return パッケージのリスト
	 */
	public List<IPackage> getPackages(IPackage rootModel, ModelFilter<IPackage> filter) {
		List<IPackage> packages = new ArrayList<IPackage>();
		packages.add(rootModel);
		return this.getPackages(rootModel, packages, filter);
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
	public List<IPackage> getPackages(IPackage thePackage, List<IPackage> packageList, ModelFilter<IPackage> filter) {
		INamedElement[] namedElements = thePackage.getOwnedElements();
		for (INamedElement namedElement : namedElements) {
			if (namedElement instanceof IPackage) {
				IPackage p = (IPackage) namedElement;
				if (filter.isEnable(p)) {
					packageList.add(p);
					getPackages(p, packageList, filter);
				}
			}
		}
		return packageList;
	}

	/**
	 * 指定パッケージ配下のクラスを、再帰的に全て取得する。
	 * 
	 * @param thePackage
	 *            指定パッケージ
	 * @return パッケージ一覧を格納したリスト
	 */
	public List<IClass> getClasses(IPackage thePackage, ModelFilter<IClass> filter) {
		return this.getClasses(thePackage, new ArrayList<IClass>(), filter);
	}

	/**
	 * 指定パッケージ配下のクラスを、再帰的に全て取得する。
	 * 
	 * @param thePackage
	 *            指定パッケージ
	 * @param classes
	 *            パッケージ一覧を格納するリスト
	 * @return パッケージ一覧を格納したリスト
	 */
	public List<IClass> getClasses(IPackage thePackage, List<IClass> classes, ModelFilter<IClass> filter) {
		if (thePackage == null) {
			return classes;
		}
		INamedElement[] namedElements = thePackage.getOwnedElements();
		for (INamedElement namedElement : namedElements) {
			if (namedElement instanceof IClass
					&& !(namedElement instanceof IUseCase || namedElement instanceof ITestCase
							|| namedElement instanceof ISubsystem || namedElement instanceof IRequirement || namedElement instanceof IAssociationClass)) {
				IClass c = (IClass) namedElement;
				if (filter.isEnable(c)) {
					classes.add(c);
					getNestedClass(c.getNestedClasses(), classes, filter);
				}
			}
			if (namedElement instanceof IPackage) {
				IPackage p = (IPackage) namedElement;
				getClasses(p, classes, filter);
			}
		}
		return classes;
	}

	private List<IClass> getNestedClass(IClass[] nestedClasses, List<IClass> classes, ModelFilter<IClass> filter) {
		if (nestedClasses == null || nestedClasses.length <= 0) {
			return classes;
		}
		for (IClass clazz : nestedClasses) {
			if (filter.isEnable(clazz)) {
				classes.add(clazz);
				getNestedClass(clazz.getNestedClasses(), classes, filter);
			}
		}
		return classes;
	}

	/**
	 * プロジェクトパッケージか判断する
	 * 
	 * @param thePackage
	 * @return
	 */
	public boolean isProjectPackage(IPackage thePackage) {
		if (thePackage.getOwner() == null) {
			return true;
		}
		return false;
	}
}
