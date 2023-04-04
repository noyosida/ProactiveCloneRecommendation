//package immediateCloneDetection;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//
//import javax.swing.AbstractAction;
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//import javax.swing.WindowConstants;
//import javax.swing.text.DefaultHighlighter;
//import javax.swing.text.Highlighter;
//import javax.swing.text.JTextComponent;
//
////ハイライトを実装するウィジェットのクラス
//public final class ResultPanel extends JPanel {
//    private static final String PATTERN = "int";
//    private static final Highlighter.HighlightPainter HIGHLIGHT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//
//
//    public ResultPanel(ArrayList<String> cloneFileList,String cloneInfo,String projectName) {
//        super(new BorderLayout());
//        final JTextArea jta = new JTextArea();
//        jta.setLineWrap(false);
//        int cloneFileIndex;
////        cloneFileList.add("cloneFileList");
//
//		String[] codeClone = cloneInfo.split("\\.",0);
//		String[] lineOfClone = codeClone[1].split("-",0);
//
//        //javaファイルを読み込みウィンドウに反映
//		try {
////			IWorkbench workbench = PlatformUI.getWorkbench();
////			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
////			IEditorPart editor = window.getActivePage().getActiveEditor();
////			IEditorInput editorInput = editor.getEditorInput();
////			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
////			IProject[] projects = root.getProjects();
//
//			//コードクローンが含まれているファイルを開きその中身を出力
//			File cloneFile = new File(cloneFileList.get(Integer.parseInt(codeClone[0])));
//			BufferedReader br;
//			br = new BufferedReader(new FileReader(cloneFile));
//			String str = br.readLine();
//			while(str != null)
//			{
//				jta.append(str);
//				jta.append("\n");
//				str = br.readLine();
//			}
//			br.close();
//
//		} catch (Exception e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//
//
//        Box box = Box.createHorizontalBox();
//        box.add(Box.createHorizontalGlue());
////        box.add(new JButton(new AbstractAction("highlight") {
////            @Override public void actionPerformed(ActionEvent e) {
////                jta.setEditable(false);
////                setHighlight(jta,lineOfClone,cloneFileList.get(Integer.parseInt(codeClone[0])),projectName);
////            }
////        }));
//        box.add(new JButton(new AbstractAction("clear") {
//            @Override public void actionPerformed(ActionEvent e) {
//                jta.setEditable(true);
//                jta.getHighlighter().removeAllHighlights();
//            }
//        }));
//        add(new JScrollPane(jta));
//        add(box, BorderLayout.SOUTH);
//        setPreferredSize(new Dimension(1000, 800));
//        setHighlight(jta,lineOfClone,cloneFileList.get(Integer.parseInt(codeClone[0])),projectName);
//    }
//
//    private static void setHighlight(JTextComponent jtc, String[] lineOfClone, String fileName,String projectName) {
//        //jtc.getHighlighter().removeAllHighlights();
//        try {
//            Highlighter highlighter = jtc.getHighlighter();
//
//            System.out.println(fileName);
//            String[] tmpFileName = fileName.split(projectName,0);
//            String ccfxFileName = tmpFileName[0] + projectName + "\\.ccfxprepdir" + tmpFileName[1];
//            if(tmpFileName.length>2){
//            	for(int i=2;i<tmpFileName.length;i++){
//            		ccfxFileName = ccfxFileName + projectName + tmpFileName[i];
//            	}
//            }
//            ccfxFileName = ccfxFileName + ".java.2_0_0_0.default.ccfxprep";
//
//            System.out.println(ccfxFileName);
//            System.out.println(projectName);
//            System.out.println(tmpFileName.length);
//
//
//            File cloneFile = new File(ccfxFileName);
////            File cloneFile = new File("C:\\pleiades-e4.5-java-jre_20151002\\pleiades\\runtime-Eclipseアプリケーション\\test\\.ccfxprepdir\\src\\test\\temp.java.java.2_0_0_0.default.ccfxprep");
//            int startIndex = Integer.parseInt(lineOfClone[0]);
//            int endIndex = Integer.parseInt(lineOfClone[1]);
//            int i;
//            String str;
//
//            BufferedReader br;
//			br = new BufferedReader(new FileReader(cloneFile));
//			for(i=1; i<startIndex; i++)str = br.readLine();
//			str = br.readLine();
//			String[] stmp = str.split("\t", 0);
//			String[] spos = stmp[0].split("\\.",0);
//			int startPos = Integer.parseInt(spos[2],16) - Integer.parseInt(spos[0],16) + 1;
//			for(i=startIndex; i<endIndex; i++)str = br.readLine();
//			String[] etmp = str.split("\t", 0);
//			String[] epos = etmp[0].split("\\.",0);
//			int endPos = Integer.parseInt(epos[2],16) - Integer.parseInt(epos[0],16) +1;
//			br.close();
//
//            highlighter.addHighlight(startPos, endPos, HIGHLIGHT_PAINTER);
////			System.out.println(Integer.parseInt(spos[2],16));
////			System.out.println(Integer.parseInt(spos[0],16));
////			System.out.println(startPos);
////			System.out.println(Integer.parseInt(epos[2],16));
////			System.out.println(Integer.parseInt(epos[0],16));
////			System.out.println(endPos);
////			highlighter.addHighlight(95, 590, HIGHLIGHT_PAINTER);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//
//    public void sampleRun(ArrayList<String> cloneFileList,String cloneInfo,String fileName,String projectName) {
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                createAndShowGUI(cloneFileList,cloneInfo,fileName,projectName);
//            }
//        });
//    }
//    public static void createAndShowGUI(ArrayList<String> cloneFileList,String cloneInfo, String fileName, String projectName) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException | InstantiationException
//               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//            ex.printStackTrace();
//        }
//        JFrame frame = new JFrame(fileName);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new ResultPanel(cloneFileList,cloneInfo,projectName));
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//}
