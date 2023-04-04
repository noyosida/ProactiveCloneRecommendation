package immediateCloneDetection.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import immediateCloneDetection.Activator;
import immediateCloneDetection.ExcuteCCFX;
import immediateCloneDetection.Result;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */

	static File ccfxFile = null;
	static File javaFileListTxt = null;
	static File modifiedFileListTxt = null;
	static File stableFileListTxt = null;
	static File groupResultCcfxd = null;
	static File groupResultTxt = null;
	static File modifiedResultCcfxd = null;
	static File modifiedResultTxt = null;

	//モニタリングのオンオフを切り替えるためのフラグ
	static int onFlag = 1;

	static Result resultView = new Result();

	static IWorkbenchWindow window;

	static IWorkspace workspace;
	static IResourceChangeListener saveListener;

	public SampleHandler() {


	}

	public static int timerFlag = 0;
	static ArrayList<String> modifiedFileList = new ArrayList<String>();
	static ArrayList<String> stableFileList = new ArrayList<String>();
	static ArrayList<String> javaFileList = new ArrayList<String>();
	static IProject Iproject;
	//前バージョンのファイルを保存しておくためのハッシュマップ
	public static HashMap<String, List<String>> map = new HashMap<String,List<String>>();

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */

	//メニューを選択時実行する処理
	public Object execute(ExecutionEvent event) throws ExecutionException {


		// アクティブエディタ（編集中のファイル）の情報を取得する。
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = window.getActivePage().getActiveEditor();
		ITextEditor textEditor = (ITextEditor)editor;

		// エディタ情報をもとに、編集中のファイルが属するプロジェクト情報を取得する。
		IFileEditorInput editorInput = (IFileEditorInput)textEditor.getEditorInput();
		IFile file = editorInput.getFile();
		IProject project = file.getProject();
		Iproject =file.getProject();

		//flagがオンの時モニタリングを開始する
		if(onFlag == 1){
			MessageDialog.openInformation(window.getShell(), "モニタリングメニュー", "モニタリングを開始します．");


			//CCFXディレクトリをプロジェクト内に埋め込むための処理
			Bundle bundle = Activator.getDefault().getBundle();
			try {
				URL fileUrl = bundle.getEntry("/items/ccfx/ccfx-win32-en/bin/ccfx.exe");
			    ccfxFile = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/fileList/javaFileList.txt");
			    javaFileListTxt = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/fileList/modifiedFileList.txt");
			    modifiedFileListTxt = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/fileList/stableFileList.txt");
			    stableFileListTxt = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/result/groupResult.ccfxd");
			    groupResultCcfxd = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/result/groupResult.txt");
			    groupResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/result/modifiedResult.ccfxd");
			    modifiedResultCcfxd = new File(FileLocator.resolve(fileUrl).toURI());
			    fileUrl = bundle.getEntry("/items/result/modifiedResult.txt");
			    modifiedResultTxt = new File(FileLocator.resolve(fileUrl).toURI());
			} catch(IOException | URISyntaxException e) {
			    e.printStackTrace();
			}

			//プロジェクト内に含まれるjavaFileListを作成
			makeJavaFileList(project);


			//メニュー起動時のアクティブエディタのファイル内容を保存
			savePrevious();

			//アクティブエディタ取得リスナ
			//これによりアクティブエディタの切り替わりを検知できる
			IWorkbenchPart active = page.getActivePart();
			//adding a listener
			IPartListener2 pl = new IPartListener2() {
			    public void partActivated(IWorkbenchPartReference ref) {
	//				savePrevious(window);
					savePrevious();
			    }

				@Override
				public void partBroughtToTop(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partClosed(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partDeactivated(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partOpened(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partHidden(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partVisible(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partInputChanged(IWorkbenchPartReference partRef) {
				}
			   };
			   page.addPartListener(pl);

			//モニタリング開始
			//保存を検知し，前回の保存から編集が加えられたファイルを特定
			workspace = ResourcesPlugin.getWorkspace();
			saveListener = new IResourceChangeListener(){
			    public void resourceChanged(IResourceChangeEvent event) {
			    	//POST_CHANGEのみを取得
			        if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			           return;
			        IResourceDelta rootDelta = event.getDelta();
			        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			           public boolean visit(IResourceDelta delta) {
			              //リソースの変更のみ取得 (追加や除去は省く)
			              if (delta.getKind() != IResourceDelta.CHANGED)
			                 return true;
			              //CONTENTのみ
			              if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
			                 return true;
			              IResource resource = delta.getResource();



			              //javaのみ
			              if (resource.getType() == IResource.FILE &&
			            		  "java".equalsIgnoreCase(resource.getFileExtension())) {
			            	  //ここで変更が加えられたファイル名を保存する
			            	  if(modifiedFileList.contains(resource.getLocation().toOSString())){
			            	  }else
			            	  modifiedFileList.add(resource.getLocation().toOSString());
			            	  if(timerFlag == 0){
			            		  //ここで変更が加えられたファイルに基づいてクローン検出を行う
			            		  //タイマーフラグを立てる
			            		  timerFlag = 1;
			            		  //タイマーを起動して数秒後に検出する処理を書く
			            		  //ExcuteCCFXクラスに内容記述
			            		  TimerTask task = new ExcuteCCFX();
			            		  Timer timer = new Timer();
			            		  timer.schedule(task, TimeUnit.SECONDS.toMillis(1));
			            	  }
			              }
			              return true;
			           }
			        };
			        try {
			           rootDelta.accept(visitor);

			        } catch (CoreException e) {
			           //open error dialog with syncExec or print to plugin log file
			        }
			    }
			};

			//上記のリスナーを起動する
			workspace.addResourceChangeListener(saveListener);

			//次回のサンプルコマンドをモニタリングオフに設定する
			onFlag = 0;
		}else{
			MessageDialog.openInformation(window.getShell(), "モニタリングメニュー", "モニタリングを終了します．");
			workspace.removeResourceChangeListener(saveListener);
			onFlag = 1;
		}
		return null;
	}

	//モニタリング開始時のソースコード情報を保存する
	public void savePrevious(/*IWorkbenchWindow window*/){
		IEditorPart editor2 = window.getActivePage().getActiveEditor();
		ITextEditor textEditor2 = (ITextEditor)editor2;

		// エディタ情報をもとに、編集中のファイルが属するプロジェクト情報を取得する。
		IFileEditorInput editorInput2 = (IFileEditorInput)textEditor2.getEditorInput();
		IFile originalFile = editorInput2.getFile();

		//選択したファイルが既に登録されていなければ，mapに追加
		if(map.containsKey(originalFile.getLocation().toOSString())){
			return;
		}else{
			List<String> list = new ArrayList<String>();
			File file = new File(originalFile.getLocation().toString());
			try {
				list = Files.readAllLines(file.toPath(), Charset.defaultCharset());
				map.put(originalFile.getLocation().toOSString(), list);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	//プロジェクト内に含まれるファイルのファイルリストを作成する
	public void makeJavaFileList(IProject project){
		//javaFileListを初期化
		javaFileList.clear();
		//プロジェクト内の全javaファイルをjavafilelist.txtに取得
		ProcessBuilder pb0 = new ProcessBuilder(ccfxFile.getPath(),"f","java","-a",Iproject.getLocation().toOSString(),"-o",javaFileListTxt.getPath());
		Process process0;
		try {
			process0 = pb0.start();
			process0.waitFor();
			process0.destroy();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(javaFileListTxt),"SJIS"));
			String fileName = br.readLine();
			while(fileName != null){
				javaFileList.add(fileName);
				fileName = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	//ExcuteCCFXクラスのメソッドから呼び出してccfxを実行するためメソッド
	public static void runCCFX(){
		//ファイルリストを作成する
		makeModifiedFileList();
		makeStableFileList();
		excuteCcfx();
	}

	//mapを取得
	public HashMap<String, List<String>> getMap(){
		return map;
	}

	//mapをすべて削除
	public void resetMap(){
		map.clear();
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		savePrevious();
	}

	//modifiedFileListを作る
	public static void makeModifiedFileList(){
		try {
			PrintWriter modifiedpw    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modifiedFileListTxt),"SJIS")));

			//ファイルリストの先頭の記述を書き込む
			modifiedpw.println("-n " + Iproject.getLocation().toOSString());
			int size = modifiedFileList.size();
			for(int i=0;i<size;i++){
				modifiedpw.println(modifiedFileList.get(i));
			}
			modifiedpw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	//stableFileListを作成する
	public static void makeStableFileList(){
		try {
			PrintWriter stablepw    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stableFileListTxt),"SJIS")));
			//ファイルリストの先頭の記述を書き込む
			stablepw.println("-n " + Iproject.getLocation().toOSString());
			int size = javaFileList.size();
			for (int i=0;i<size;i++){
				if(modifiedFileList.contains(javaFileList.get(i))){
				}else{
					stableFileList.add(javaFileList.get(i));
					stablepw.println(javaFileList.get(i));
				}
			}
			stablepw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static void excuteCcfx(){
		try {
			//修正が加えられたファイル内のみからクローン検出
			ProcessBuilder pb1 = new ProcessBuilder(ccfxFile.getPath(),"d","java","-i",modifiedFileListTxt.getPath(),"-o",modifiedResultCcfxd.getPath());
			Process process1 = pb1.start();
			process1.waitFor();
			process1.destroy();

			//クローン検出結果を見える化する
			ProcessBuilder pb2 = new ProcessBuilder(ccfxFile.getPath(),"p",modifiedResultCcfxd.getPath(),"-o",modifiedResultTxt.getPath());
			Process process2 = pb2.start();
			process2.waitFor();
			process2.destroy();

			//ファイルグループ間のクローン検出
			ProcessBuilder pb3 = new ProcessBuilder(ccfxFile.getPath(),"d","java","-i",modifiedFileListTxt.getPath(),"-is","-i",stableFileListTxt.getPath(),"-w","w-f-g+","-o",groupResultCcfxd.getPath());
			Process process3 = pb3.start();
			process3.waitFor();
			process3.destroy();

			//クローン検出結果を見える化する
			ProcessBuilder pb4 = new ProcessBuilder(ccfxFile.getPath(),"p",groupResultCcfxd.getPath(),"-o",groupResultTxt.getPath());
			Process process4 = pb4.start();
			process4.waitFor();
			process4.destroy();

			//resultの内容を変更
			resultView.checkDiff();
			resultView.updateResult();

			//modifiedFileListの中身をすべて削除
			modifiedFileList.clear();
			//stableFileListの中身をすべて削除
			stableFileList.clear();

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
