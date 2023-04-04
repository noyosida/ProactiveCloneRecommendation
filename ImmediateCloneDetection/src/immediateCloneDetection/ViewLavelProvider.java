package immediateCloneDetection;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

//ラベルのプロバイダー
public class ViewLavelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object obj, int index){
		CloneInfoModel model = (CloneInfoModel)obj;
		switch(index){
		case 0: return model.getClonesetId();
		case 1: return model.getCloneInfo();
		case 2: return model.getLineInfo();
		default:return "";
		}
	}
	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}

	public Image getImage(Object obj) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(
															ISharedImages.IMG_OBJ_ELEMENT);
	}
}
