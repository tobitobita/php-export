package dsk.php_export.core.utils;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;

public class SkeletonCodeTools {
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
		String str = "class";
		String[] stereotypes = element.getStereotypes();
		for (String stereotype : stereotypes) {
			if ("interface".equals(stereotype)) {
				str = "interface";
				break;
			}
		}
		return str;
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
		String str = operation.getName();
		String[] stereotypes = operation.getStereotypes();
		for (String stereotype : stereotypes) {
			if ("create".equals(stereotype)) {
				str = "__construct";
				break;
			}
		}
		return str;
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
}
