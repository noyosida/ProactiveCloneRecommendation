package immediateCloneDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import immediateCloneDetection.handlers.SampleHandler;

//CCFinderの出力結果を表示するview
public class Result extends ViewPart {
	static TableViewer tableViewer;
	static TextViewer textViewer;
	static ArrayList<String> cloneFileList = new ArrayList<String>();
	File groupResultTxt = null;
	File modifiedResultTxt = null;
	static HashMap<String, HashMap<Integer,Boolean>> diffMap = new HashMap<String,HashMap<Integer,Boolean>>();


	public Result() {
		// TODO 自動生成されたコンストラクター・スタブ
		cloneFileList.add("cloneFileList");
		try {
			Bundle bundle = Activator.getDefault().getBundle();
	    	//コンストラクタでresultファイルを定義
	    	URL fileUrl = bundle.getEntry("/items/result/modifiedResult.txt");
			modifiedResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
			fileUrl = bundle.getEntry("/items/result/groupResult.txt");
			groupResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
		} catch (URISyntaxException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void updateResult(){
	    try {
			Bundle bundle = Activator.getDefault().getBundle();
	    	//コンストラクタでresultファイルを定義
	    	URL fileUrl = bundle.getEntry("/items/result/modifiedResult.txt");
			modifiedResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
			fileUrl = bundle.getEntry("/items/result/groupResult.txt");
			groupResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
			Control control = textViewer.getControl();
			if ((control != null)) {
				control.getDisplay().syncExec(new Runnable() {
					public void run() {
						updateTextViewer();					}
				});
			}
			Control control2 = tableViewer.getControl();
			if ((control2 != null)) {
				control2.getDisplay().syncExec(new Runnable() {
					public void run() {
						updateTable();
					}
				});
			}
		} catch (URISyntaxException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	//最初にviewを構成するための処理
	@Override
	public void createPartControl(Composite parent) {
		try {

			textViewer = new TextViewer(parent,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			updateTextViewer();

			tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
			setTableViewer();

			//resultファイルが書き換えられることを監視するwatcherを作成
//			Bundle bundle = Activator.getDefault().getBundle();
//	    	URL Url = bundle.getEntry("/items/result");
//	    	File kari = new File(FileLocator.resolve(Url).toURI());
//			Watcher watcher = new Watcher(this, tableViewer, textViewer);
//			watcher.setWatchPath(kari.getPath());
//			Thread thread = new Thread(watcher);
//			thread.start();

		} catch (Exception e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
		}
	}



	//TextViewの内容を更新する
	//TextViewではModifiedファイルとStableファイルのファイルリストを表示する
	public void updateTextViewer(/*TextViewer textViewer*/){
		try{
			cloneFileList.clear();
			cloneFileList.add("cloneFileList");

			//modifiedファイル
			BufferedReader mbr = new BufferedReader(new InputStreamReader(new FileInputStream(modifiedResultTxt),"SJIS"));
			String mstr = mbr.readLine();
			String str1 = "Modified File List";
			while(mstr !=null ){
				if(mstr.equals("source_files {")){
					while(!(mstr.equals(null))){
						mstr = mbr.readLine();
						str1 = str1 + "\n" + mstr;
						if(mstr.equals("}"))break;
					}
				}
				mstr = mbr.readLine();
				if(mstr.equals("clone_pairs {"))break;
			}

			//グループファイル(つまり全ファイル)
			BufferedReader gbr = new BufferedReader(new InputStreamReader(new FileInputStream(groupResultTxt),"SJIS"));
			String gstr = gbr.readLine();
			str1 = str1 + "\n" + "Group File List";
			while(gstr !=null ){
				if(gstr.equals("source_files {")){
					while(!(gstr.equals(null))){
						gstr = gbr.readLine();
						str1 = str1 + "\n" + gstr;
						if(gstr.equals("}"))break;
						String[] strFile = gstr.split("\t",0);
						if(cloneFileList.contains(strFile[1])){
						}else{
							cloneFileList.add(strFile[1]);
						}
					}
				}
				gstr = gbr.readLine();
				if(gstr.equals("clone_pairs {"))break;
			}
			Document doc = new Document(str1);
			textViewer.setDocument(doc);
			mbr.close();
			gbr.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			}
	}

	//TableViewerの内容を設定する
	public void setTableViewer(/*TableViewer tableViewer*/){
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText("クローンセットID");
		column1.setWidth(150);

		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText("クローン情報");
		column2.setWidth(180);

		TableColumn column3 = new TableColumn(table, SWT.NULL);
		column3.setText("行情報");
		column3.setWidth(180);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new ViewLavelProvider());
		updateTable();


		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				CloneInfoModel model = (CloneInfoModel) tableViewer.getElementAt(tableViewer.getTable().getSelectionIndex());
				setHighlight(model.getCloneInfo());
				//checkDiff();
			}
		});
	}

	//更新があったテーブルの値をセットする
	public void updateTable(/*TableViewer tableViewer*/){
		tableViewer.setInput(getItems());
	}

	public String getCloneLine(String cloneInfo){
		// エディタ情報をもとに、編集中のファイルが属するプロジェクト情報を取得する。
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart editor = window.getActivePage().getActiveEditor();
		ITextEditor sourceEditor = (ITextEditor)editor;
		IFileEditorInput editorInput = (IFileEditorInput)sourceEditor.getEditorInput();
		IFile file = editorInput.getFile();
		IProject project = file.getProject();

		String[] codeClone = cloneInfo.split("\\.",0);
		String[] lineOfClone = codeClone[1].split("-",0);
        String[] tmpFileName = cloneFileList.get(Integer.parseInt(codeClone[0])).split(project.getName(),0);
        String ccfxFileName = tmpFileName[0] + project.getName() + "\\.ccfxprepdir" + tmpFileName[1];
        String openFileName = project.getName() + tmpFileName[1];
        if(tmpFileName.length>2){
        	for(int i=2;i<tmpFileName.length;i++){
        		ccfxFileName = ccfxFileName + project.getName() + tmpFileName[i];
        		openFileName = openFileName + project.getName() + tmpFileName[i];
        	}
        }
        //CCFXの.ccfxprep結果ファイル名を取得
        ccfxFileName = ccfxFileName + ".java.2_0_0_0.default.ccfxprep";
     	File cloneFile = new File(ccfxFileName);
     	//ここから開始行と終了行の探索を開始
        int startIndex = Integer.parseInt(lineOfClone[0]);
        int endIndex = Integer.parseInt(lineOfClone[1]);
        int i;
        String str;
        BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(cloneFile));
			for(i=1; i<startIndex; i++)str = br.readLine();
			str = br.readLine();
			int offset;
			//タイムラグ対策
			if(str == null){
				offset = 0;
			}else{
				String[] stmp = str.split("\t", 0);
				String[] spos = stmp[0].split("\\.",0);
				offset = Integer.parseInt(spos[0],16);
			}
			for(i=startIndex; i<endIndex; i++)str = br.readLine();
			int endPos;
			//タイムラグ対策
			if(str == null){
			endPos=0;
			}else{
			String[] etmp = str.split("\t", 0);
			String[] epos = etmp[0].split("\\.",0);
			endPos = Integer.parseInt(epos[0],16);
			}
			br.close();

			//行情報を"開始行ー終了行"という形で格納するための変数
			String line = Integer.toString(offset) + "-" + Integer.toString(endPos);
			return line;
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	//ハイライト処理
	private void setHighlight(String cloneInfo) {
		try {
			// エディタ情報をもとに、編集中のファイルが属するプロジェクト情報を取得する。
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editor = window.getActivePage().getActiveEditor();
			ITextEditor sourceEditor = (ITextEditor)editor;
			IFileEditorInput editorInput = (IFileEditorInput)sourceEditor.getEditorInput();
			IFile file = editorInput.getFile();
			IProject project = file.getProject();

			String[] codeClone = cloneInfo.split("\\.",0);
			String[] lineOfClone = codeClone[1].split("-",0);
            String[] tmpFileName = cloneFileList.get(Integer.parseInt(codeClone[0])).split(project.getName(),0);
            String ccfxFileName = tmpFileName[0] + project.getName() + "\\.ccfxprepdir" + tmpFileName[1];
            String openFileName = project.getName() + tmpFileName[1];
            if(tmpFileName.length>2){
            	for(int i=2;i<tmpFileName.length;i++){
            		ccfxFileName = ccfxFileName + project.getName() + tmpFileName[i];
            		openFileName = openFileName + project.getName() + tmpFileName[i];
            	}
            }
            ccfxFileName = ccfxFileName + ".java.2_0_0_0.default.ccfxprep";
			// アクティブエディタ（編集中のファイル）の情報を取得する。
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile ifile = root.getFile(new Path(openFileName));
			IWorkbenchPage page = window.getActivePage();

			IEditorPart editorPart = IDE.openEditor(page, ifile);
			ITextEditor textEditor = (ITextEditor)editorPart;



			//アノテーションの削除
			AnnotationModel annotationModel = (AnnotationModel)textEditor.getDocumentProvider().getAnnotationModel(textEditor.getEditorInput());
			Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
			while(annotationIterator.hasNext()) {
				Annotation currentAnnotation = annotationIterator.next();
				if(currentAnnotation.getType().equals(SliceAnnotation.EXTRACTION)) {
					annotationModel.removeAnnotation(currentAnnotation);
				}
			}

			//CCFinderの出力結果を分析し，コードクローン部分をハイライト
            File cloneFile = new File(ccfxFileName);
            int startIndex = Integer.parseInt(lineOfClone[0]);
            int endIndex = Integer.parseInt(lineOfClone[1]);
            int i;
            String str;

            BufferedReader br;
			br = new BufferedReader(new FileReader(cloneFile));

			for(i=1; i<startIndex; i++)str = br.readLine();
			str = br.readLine();
			String[] stmp = str.split("\t", 0);
			String[] spos = stmp[0].split("\\.",0);
			int offset = Integer.parseInt(spos[2],16);
			for(i=startIndex; i<endIndex; i++)str = br.readLine();
			String[] etmp = str.split("\t", 0);
			String[] epos = etmp[0].split("\\.",0);
			int endPos = Integer.parseInt(epos[2],16);
			int length = endPos - offset;
			br.close();

			Position position = new Position(offset, length);
			SliceAnnotation annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, null);
			annotationModel.addAnnotation(annotation, position);
			textEditor.setHighlightRange(offset, length, true);
		} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
		}
	}

	private List<CloneInfoModel> getItems(){
		List<CloneInfoModel> list = new ArrayList<CloneInfoModel>();
		List<CloneInfoModel> groupResultList = new ArrayList<CloneInfoModel>();

		CloneInfoModel model = new CloneInfoModel();


		try{
			//ModifiedCloneに対する処理
			//diffが存在するクローンセットIDを特定する
			BufferedReader mDiffbr = new BufferedReader(new FileReader(modifiedResultTxt));
			String str = mDiffbr.readLine();
			while(str != null){
				if(str.equals("clone_pairs {"))break;
				str = mDiffbr.readLine();
			}
			int cloneSetId = 0;
			str = mDiffbr.readLine();
			ArrayList<String> mCloneID = new ArrayList<String>();
			while(str != null){
				if(str.equals("}"))break;
				cloneSetId = Integer.parseInt(str.split("\t", 0)[0]);
				while(str != null){
					String[] strFile = str.split("\t",0);
					if(mCloneID.contains(strFile[0])){
					}else{
						 //ここでdiffを取って，変更部分とclone部分が一致するか判定する
						if(isDiff(strFile[1],getCloneLine(strFile[1]))){
							mCloneID.add(strFile[0]);
						}
					}
					str = mDiffbr.readLine();
					if(str.equals("}"))break;
					if(cloneSetId != Integer.parseInt(str.split("\t", 0)[0]))break;
				}
			}
			mDiffbr.close();

			//ccfxの出力結果をviewに反映
			//Diffがあったもののみを反映する
			model = new CloneInfoModel();
			model.setCloneInfo("ModifiedFileClone");
			list.add(model);
			BufferedReader mbr = new BufferedReader(new FileReader(modifiedResultTxt));
			str = mbr.readLine();
			while(str != null){
				if(str.equals("clone_pairs {"))break;
				str = mbr.readLine();
			}
			str = mbr.readLine();
			ArrayList<String> mClone = new ArrayList<String>();
			while(str != null){
				if(str.equals("}"))break;
				cloneSetId = Integer.parseInt(str.split("\t", 0)[0]);
				while(str != null){
					String[] strFile = str.split("\t",0);
					if(mClone.contains(strFile[1])){
					}else{
						//diffのあるものだけを保存していく
						if(mCloneID.contains(strFile[0])){
							mClone.add(strFile[1]);
							model = new CloneInfoModel();
							model.setClonesetId(strFile[0]);
							model.setCloneInfo(strFile[1]);
							model.setLineInfo(getCloneLine(strFile[1]));
							list.add(model);
						}
					}
					str = mbr.readLine();
					if(str.equals("}"))break;
					if(cloneSetId != Integer.parseInt(str.split("\t", 0)[0]))break;
				}
			}
			mbr.close();

			//Result Viewに表示するクローン情報をクローンセットIDでソート
			list.sort((a,b)-> a.getIntId() - b.getIntId()); /*ラムダ式*/


			//groupCloneに対しての処理
			BufferedReader gDiffbr = new BufferedReader(new FileReader(groupResultTxt));
			str = gDiffbr.readLine();
			while(str != null){
				if(str.equals("clone_pairs {"))break;
				str = gDiffbr.readLine();
			}
			cloneSetId = 0;
			str = gDiffbr.readLine();
			ArrayList<String> gCloneID = new ArrayList<String>();
			while(str != null){
				if(str.equals("}"))break;
				cloneSetId = Integer.parseInt(str.split("\t", 0)[0]);
				while(str != null){
					String[] strFile = str.split("\t",0);
					if(gCloneID.contains(strFile[0])){
					}else{
						 //ここでdiffを取って，変更部分とclone部分が一致するか判定する
						if(isDiff(strFile[1],getCloneLine(strFile[1]))){
							gCloneID.add(strFile[0]);
						}
					}
					str = gDiffbr.readLine();
					if(str.equals("}"))break;
					if(cloneSetId != Integer.parseInt(str.split("\t", 0)[0]))break;
				}
			}
			gDiffbr.close();
			//ccfxの出力結果をviewに反映
			model = new CloneInfoModel();
			model.setCloneInfo("BetweenGroupClone");
			list.add(model);

			BufferedReader gbr = new BufferedReader(new FileReader(groupResultTxt));
			str = gbr.readLine();
			while(str != null){
				if(str.equals("clone_pairs {"))break;
				str = gbr.readLine();
			}
			str = gbr.readLine();
			ArrayList<String> gClone = new ArrayList<String>();
			while(str != null){
				if(str.equals("}"))break;
				cloneSetId = Integer.parseInt(str.split("\t", 0)[0]);
				while(str != null){
					String[] strFile = str.split("\t",0);
					if(gClone.contains(strFile[1])){
					}else{
						//ここでdiffを取って変更部分とclone部分が一致するかどうか求める
						if(gCloneID.contains(strFile[0])){
//							System.out.println("aa");
							gClone.add(strFile[1]);
							model = new CloneInfoModel();
							model.setClonesetId(strFile[0]);
							model.setCloneInfo(strFile[1]);
							model.setLineInfo(getCloneLine(strFile[1]));
							groupResultList.add(model);
						}
					}
					str = gbr.readLine();
					if(str.equals("}"))break;
					if(cloneSetId != Integer.parseInt(str.split("\t", 0)[0]))break;
				}
			}
			gbr.close();

			//Result Viewに表示するクローン情報をクローンセットIDでソート
			groupResultList.sort((a,b)-> a.getIntId() - b.getIntId()); /*ラムダ式*/
			list.addAll(groupResultList);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	//2バージョン間のdiffを取り，そのファイル名と位置情報をdiffMapに保存する
	public void checkDiff(){
		SampleHandler handle = new SampleHandler();
		HashMap<String, List<String>> map = new HashMap<String,List<String>>();
		map = handle.getMap();

		diffMap.clear();
		int size = cloneFileList.size();
        for (int i = 0; i < size; i++) {
            if(map.containsKey(cloneFileList.get(i))){
            	//変更があった位置を保存しておくMap
            	//List<Integer> diffList = new ArrayList<Integer>();
            	HashMap<Integer,Boolean> diffCollection = new HashMap<Integer,Boolean>();
            	//oldLinesに前バージョンのソースコードを格納
            	List<String> oldLines = map.get(cloneFileList.get(i));
            	//newLinesに現在のソースコードを格納
            	File newFile = new File(cloneFileList.get(i));
            	List<String> newLines = new ArrayList<String>();
            	try {
					BufferedReader br = new BufferedReader(new FileReader(newFile));
					String str = br.readLine();
					while(str != null){
						newLines.add(str);
						str = br.readLine();
					}
					br.close();

					//ここで1つのファイルに対してdiffを取る
					Patch patch = DiffUtils.diff(oldLines, newLines);
					for (Delta delta : patch.getDeltas()) {
						diffCollection.put(delta.getRevised().getPosition() + 1, true);
			        }
					//diffのリストをmapに格納
					diffMap.put(cloneFileList.get(i), diffCollection);
				} catch (FileNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
            }
        }
        /*
         * ここにhashmapを空にするメソッドを書き加える
         */
        handle.resetMap();
	}

	public boolean isDiff(String cloneInfo,String cloneLine){
		String[] codeClone = cloneInfo.split("\\.",0);
		if(diffMap.containsKey(cloneFileList.get(Integer.parseInt(codeClone[0])))){
			HashMap<Integer,Boolean> tmpMap = diffMap.get(cloneFileList.get(Integer.parseInt(codeClone[0])));
			String[] line = cloneLine.split("-",0);
			for(int i = Integer.parseInt(line[0]);i <= Integer.parseInt(line[1]);i++){
				if(tmpMap.containsKey(i)){
					return true;
				}
			}
		}else{
			return false;
		}
		return false;
	}

	@Override
	public void setFocus() {
		// TODO 自動生成されたメソッド・スタブ
		tableViewer.getControl().setFocus();
	}

}
