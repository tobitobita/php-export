package dsk.export.tools;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IAssociationClass;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IRequirement;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITestCase;
import com.change_vision.jude.api.inf.model.IUseCase;

public class SkeletonCodeTools {
	private static final String CLASS_TYPE_CLASS = "class";
	private static final String CLASS_TYPE_INTERFACE = "interface";

	private static final String CONSTRUCTOR = "__construct";

	/**
	 * クラスの名前をフルパスで取得する
	 * 
	 * @param iClass
	 *            クラス
	 * @return クラス名（フルパス）
	 */
	public String getNamespace(IClass iClass) {
		StringBuilder sb = new StringBuilder();
		IElement owner = iClass.getOwner();
		while (owner != null && owner instanceof INamedElement && owner.getOwner() != null) {
			// namespace Zend\Cache;
			sb.insert(0, String.format("%s\\", ((INamedElement) owner).getName()));
			owner = owner.getOwner();
		}
		if (0 < sb.length()) {
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}

	public String getVisibilityString(INamedElement element) {
		String str = "";
		if (element.isPublicVisibility()) {
			str = INamedElement.PUBLIC_VISIBILITY;
		} else if (element.isPackageVisibility()) {
			str = INamedElement.PACKAGE_VISIBILITY;
		} else if (element.isProtectedVisibility()) {
			str = INamedElement.PROTECTED_VISIBILITY;
		} else if (element.isPrivateVisibility()) {
			str = INamedElement.PRIVATE_VISIBILITY;
		}
		return str;
	}

	public String getClassTypeString(INamedElement element) {
		if (element.hasStereotype(CLASS_TYPE_INTERFACE)) {
			return CLASS_TYPE_INTERFACE;
		}
		return CLASS_TYPE_CLASS;
	}

	public String getFieldString(IAttribute attr) {
		return addDollar(attr.getName());
	}

	private static String addDollar(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		if (str.startsWith("$")) {
			return str;
		}
		return "$" + str;
	}

	public String getOperationString(IOperation operation) {
		// 戻り値がない or createの場合
		if (null == operation.getReturnType()) {
			return CONSTRUCTOR;
		}
		if (operation.hasStereotype("create")) {
			return CONSTRUCTOR;
		}
		return operation.getName();
	}

	public String createParameterString(IParameter[] parameters) {
		StringBuilder sb = new StringBuilder();
		for (IParameter p : parameters) {
			sb.append(", ");
			sb.append(addDollar(p.getName()));
		}
		if (0 < sb.length()) {
			return sb.substring(2);
		}
		return sb.toString();
	}

	public String getReturnString(IOperation operation) {
		IClass clazz = operation.getReturnType();
		if (null == clazz) {
			return "";
		}
		String str = "null";
		String type = clazz.getName();
		if ("char".equalsIgnoreCase(type) || "byte".equalsIgnoreCase(type) || "short".equalsIgnoreCase(type)
				|| "int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || "long".equalsIgnoreCase(type)
				|| "float".equalsIgnoreCase(type) || "double".equalsIgnoreCase(type)) {
			str = "0";
		} else if ("bool".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			str = "false";
		} else if ("string".equalsIgnoreCase(type)) {
			str = "\"\"";
		} else if ("void".equalsIgnoreCase(type)) {
			str = "";
		}
		return str;
	}

	public boolean isClass(IElement model) {
		if (model instanceof IClass
				&& !(model instanceof IUseCase || model instanceof ITestCase || model instanceof ISubsystem
						|| model instanceof IRequirement || model instanceof IAssociationClass)) {
			return true;
		}
		return false;
	}

	public String createGeneralizationsStr(IClass clazz) {
		StringBuilder classesStr = new StringBuilder();
		StringBuilder ifStr = new StringBuilder();
		IGeneralization[] generalizations = clazz.getGeneralizations();
		for (IGeneralization g : generalizations) {
			IClass superType = g.getSuperType();
			if (superType.hasStereotype(CLASS_TYPE_INTERFACE)) {
				ifStr.append(", ");
				ifStr.append(g.getSuperType().getName());
			} else {
				classesStr.append(", ");
				classesStr.append(g.getSuperType().getName());
			}
		}
		StringBuilder sb = new StringBuilder();
		if (clazz.hasStereotype(CLASS_TYPE_INTERFACE)) {
			if (0 < ifStr.length()) {
				sb.append(" extends ");
				sb.append(ifStr.substring(2));
			}
		} else {
			if (0 < classesStr.length()) {
				sb.append(" extends ");
				sb.append(classesStr.substring(2));
			}
			if (0 < ifStr.length()) {
				sb.append(" implements ");
				sb.append(ifStr.substring(2));
			}
		}
		return sb.toString();
	}

	public String createDefinitionStr(INamedElement namedElement, String space) {
		String definition = namedElement.getDefinition();
		if (StringUtils.isEmpty(definition)) {
			definition = "TODO Auto-generated comment.";
		}
		return String.format(space + "/**\n" + space + " * %s\n" + space + " */",
				definition.replace("\n", "\n" + space + " * "));
	}

	public String toAbstractStr(IClass clazz) {
		if (clazz.isAbstract()) {
			return "abstract ";
		}
		return "";
	}
}
