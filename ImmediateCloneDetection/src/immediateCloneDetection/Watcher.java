//package immediateCloneDetection;
//
//import java.io.IOException;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardWatchEventKinds;
//import java.nio.file.WatchEvent;
//import java.nio.file.WatchKey;
//import java.nio.file.WatchService;
//
//import org.eclipse.jface.text.TextViewer;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.swt.widgets.Control;
//
//public class Watcher implements Runnable {
//
//	Result rs;
//	TableViewer table;
//	TextViewer text;
//	private boolean running = true;
//
//	public Watcher(Result result,TableViewer tableViewer,TextViewer textViewer) {
//		// TODO 自動生成されたコンストラクター・スタブ
//		rs = result;
//		table = tableViewer;
//		text = textViewer;
//	}
//
//	private String watchPath;
//
//	public String getWatchPath() {
//		return watchPath;
//	}
//
//	public void setWatchPath(String watchPath) {
//		this.watchPath = watchPath;
//	}
//
//
//	public void run() {
//		Path dir = Paths.get(watchPath);
//
//		WatchService watcher;
//		try {
//			watcher = FileSystems.getDefault().newWatchService();
//			dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
//
//			while (running) {
//				WatchKey watchKey = watcher.take();
//
//				Path name = null;
////				List<WatchEvent<?>> eventList = watchKey.pollEvents();
////				for(int j = 0; j< eventList.size();j++){
////					System.out.println(eventList.get(j).context().toString());
////				}
////				System.out.println("aaa");
////				for(int i = 0; i < eventList.size(); i++){
//				for (WatchEvent<?> event : watchKey.pollEvents()) {
////					WatchEvent<?> event = eventList.get(i);
//					WatchEvent.Kind<?> kind = event.kind();
//
//
//					//A special event to indicate that events may have been lost or discarded.
//					if (kind == StandardWatchEventKinds.OVERFLOW) {
//						continue;
//					}
//
//					name = (Path) event.context();
////					System.out.println(name.toString());
//
//					if(kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY){
//					}
//
//					//result.txtの内容が変わった際の処理
//					if (name.toString().equals("modifiedResult.txt") || name.toString().equals("groupResult.txt")){
//						Control control = table.getControl();
////						watcher.close();
//						//viewを別スレッドから変更するために必要な処理
//						if ((control != null)) {
//							control.getDisplay().syncExec(new Runnable() {
//								public void run() {
//										rs.checkDiff();
//										rs.updateResult();
////										rs.updateTextViewer(text);
////										rs.updateTable(table);
//										watchKey.cancel();
//								}
//							});
//						}
//						break;
//					}
//				}
//
//				watchKey.reset();
//
//				//監視キーをリセット
//				boolean valid = watchKey.reset();
//				if(!valid){
//					break;
//				}
//
//			}
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//
//
//	}
//
//}
