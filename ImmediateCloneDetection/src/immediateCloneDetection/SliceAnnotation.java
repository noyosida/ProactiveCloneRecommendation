package immediateCloneDetection;

import org.eclipse.jface.text.source.Annotation;

public class SliceAnnotation extends Annotation {
	public static final String EXTRACTION = "immediateCloneDetection.extractionAnnotation";
	//public static final String DUPLICATION = "immediateCloneDetection.duplicationAnnotation";

	public SliceAnnotation(String type, String text) {
		super(type, false, text);
	}
}
