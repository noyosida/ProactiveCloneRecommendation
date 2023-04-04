package immediateCloneDetection;


//viewに表示する属性を保存
public class CloneInfoModel {

	private String clonesetId = null;	//クローンセットID
	private String cloneInfo = null;	//クローンの情報
	int intId = 0;
	private String lineInfo = null;

	public String getClonesetId(){
		return this.clonesetId;
	}

	public String getCloneInfo(){
		return this.cloneInfo;
	}

	public int getIntId(){
		return this.intId;
	}

	public String getLineInfo(){
		return this.lineInfo;
	}

	public void setClonesetId(String fileId){
		this.clonesetId = fileId;
		setIntId(clonesetId);
	}

	public void setCloneInfo(String cloneInfo){
		this.cloneInfo = cloneInfo;
	}

	public void setIntId(String clonesetId){
		this.intId = Integer.parseInt(clonesetId);
	}

	public void setLineInfo(String line){
		this.lineInfo = line;
	}
}
