package com.example.cherrylixh.ottatvtest;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AnlysysXml {
    public ArrayList<String[]> getLogInfo() throws SAXException {
        ArrayList<String[]> logInfos = new ArrayList<String[]>();

        //从xml文档中获取DOM解析器
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //从DOM工厂获取DOM解析器
            DocumentBuilder db = dbf.newDocumentBuilder();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            String filename = "OttAtv" + File.separator + "Test.xml";
            File file = new File(path + filename);
            Document doc = db.parse(file);
            //获得根节点
            Element root = doc.getDocumentElement();
            //获取文档节点名称为testcase的所有节点，返回一个节点集合
            NodeList nls = root.getChildNodes();
            System.out.println("一共有" + nls.getLength() + "个节点");

            //遍历
            for (int i = 0; i < nls.getLength(); i++) {
                Node nd = nls.item(i);
                String[] logInfo = new String[6];
                if (nd.getNodeName().equals("testcase")) {
                    System.out.println("===这是第" + (i + 1) + "个testcase节点的开始===");
                    //获取string的属性ID
                    String id = nd.getAttributes().getNamedItem("ID").getNodeValue();
                    System.out.println(id);
                    logInfo[0] = id;
                    String classname = nd.getAttributes().getNamedItem("classname").getNodeValue();
                    System.out.println(classname);
                    logInfo[1] = classname;
                    String casename = nd.getAttributes().getNamedItem("casename").getNodeValue();
                    System.out.println(casename);
                    logInfo[2] = casename;
                    String time = nd.getAttributes().getNamedItem("time").getNodeValue();
                    System.out.println(time);
                    logInfo[3] = time;
                    Node nd1 = nd.getFirstChild();
                    String status = "";
                    String message = "";
                    if (nd1.getNodeName().equals("result")) {
                        message = nd1.getNodeValue();
                        status = nd1.getAttributes().getNamedItem("message").getNodeValue();
                    } else {
                        status = nd1.getNodeName();
                        message = nd1.getAttributes().getNamedItem("message").getNodeValue();
                    }
                    logInfo[4] = status;
                    logInfo[5] = message;
                    System.out.println(status);
                    System.out.println(message);
                    logInfos.add(logInfo);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logInfos;
    }
   public void xmlToHtml(){
       ArrayList<String[]> logInfos = new ArrayList<>();
       try {
           logInfos = new AnlysysXml().getLogInfo();
       } catch (SAXException e) {
           Log.v("loginfo","not end");
       }
       for(int i=0;i<logInfos.size();i++) {
           Log.i("loginfo", logInfos.get(i).toString());
       }
       String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
       String filename =path+ "OttAtv" + File.separator + "testResult.html";
       StringBuilder sb=new StringBuilder();
       PrintStream printStream=null;
       int passNum=0,failNum=0;
       float time=0.0f;
       for(int i=0;i<logInfos.size();i++){
           if(logInfos.get(i)[4]=="pass"){
               passNum++;
           }else{
               failNum++;
           }
       time+=Float.parseFloat(logInfos.get(i)[3]);
       }
       int hh=(int)time/(60*60);
       int mm=(int)(time-hh*3600)/60;
       float ss=time-hh*3600-mm*60;
       try {
           printStream=new PrintStream(new FileOutputStream(filename));
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       sb.append("<html>");
       sb.append("<head>");
       sb.append("<title>测试报告</title>");
       sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
       //样式内容
       sb.append("<style type=\"text/css\">");
       sb.append(".tablename table th {background:#8FBC8F}");
       sb.append(".tablename table tr{ background:#FAEBD7;text-align:center}");
       sb.append("</style></head>");
       //样式结尾
       //一个标题
       sb.append("<h1>测试报告</h1>");
       //一个文本段落
       sb.append("<p style=\"font-size:20px\">"+"total "+logInfos.size()+" passing "+passNum+" failing "+failNum+" 通过率"+passNum/logInfos.size()*100+"%,"+"运行时长："+hh+" hours "+mm+" minutes "+ss+" seconds"+"</p>");
       //主体部分div
       sb.append("<div class=\"tablename\">");
       //一个table
       sb.append("<table width=\"500\"  height=\"100\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">");
       sb.append("<th>ID</th><th>测试项目</th><th>case</th><th>执行时间(秒)</th><th>执行状态</th><th>错误信息</th></tr>");
       for(int i=0;i<logInfos.size();i++){
           for(int j=0;j<logInfos.get(i).length;j++){
               sb.append("<td>"+logInfos.get(i)[j]+"</td>");
           }
           sb.append("</tr>");
       }

       sb.append("</tr></table>");
       sb.append("</div></body></html>");
       printStream.println(sb.toString());
   }
    /*
    public void xmlToExcel(){
        ArrayList<String[]> logInfos = new ArrayList<>();
        try {
            logInfos = new AnlysysXml().getLogInfo();
        } catch (SAXException e) {
            Log.v("loginfo","not end");
        }
        for(int i=0;i<logInfos.size();i++) {
            Log.i("loginfo", logInfos.get(i).toString());
        }
        //创建excel文件
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        String filename =path+ "OttAtv" + File.separator + "log.xls";
        File file=new File(filename);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建输出流
        FileOutputStream out=null;
        //创建工作簿
        HSSFWorkbook workbook=new HSSFWorkbook();
        //创建第一个sheet工作表
        HSSFSheet sheet=workbook.createSheet("testlog");
        //表头样式
        HSSFCellStyle hearders=workbook.createCellStyle();
        //设置表头背景颜色
        hearders.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
        hearders.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //设置单元格填充样式
        hearders.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        hearders.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        hearders.setBorderRight(HSSFCellStyle.BORDER_THIN);
        hearders.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置单元格为水平对齐的类型
        hearders.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直居中
        hearders.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //设置自动换行
        hearders.setWrapText(true);
        //错误的文本样式

        HSSFCellStyle failStyle=workbook.createCellStyle();
        failStyle.setFillForegroundColor(IndexedColors.RED.index);
       failStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      //设置单元格填充样式
        failStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        failStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        failStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        failStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置单元格为水平对齐的类型
        failStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直居中
        failStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //设置自动换行
        failStyle.setWrapText(true);

        //正确的case样式
        HSSFCellStyle passStyle=workbook.createCellStyle();
        //设置文本颜色
        passStyle.setFillForegroundColor(IndexedColors.GREEN.index);
        passStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //设置单元格边框填充样式
        passStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        passStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        passStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        passStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置单元格为水平对齐的类型
        passStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直居中
        passStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //设置自动换行
        passStyle.setWrapText(true);
        //设置字体样式
        HSSFFont font=workbook.createFont();
        font.setColor(HSSFFont.COLOR_NORMAL);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)14);
       // font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        hearders.setFont(font);
        failStyle.setFont(font);
        passStyle.setFont(font);
        String[] firstRow={"ID","测试项目","case","执行时间(秒)","执行状态","错误信息"};
        //在sheet中添加表头第一行
        HSSFRow row=sheet.createRow(0);
        //创建单元格，设置表头
        for(int i=0;i<firstRow.length;i++){
            HSSFCell cell=row.createCell(i);
            cell.setCellStyle(hearders);
            cell.setCellValue(firstRow[i].toString());
            if(i!=0){
                sheet.setColumnWidth(i, 30 * 256);
            }
        }
        //将数据写入到excel中
        for(int i=0;i<logInfos.size();i++) {
            String[] logInfo = logInfos.get(i);
            HSSFRow row1 = sheet.createRow(i + 1);
            if (Arrays.asList(logInfo).contains("pass")) {
                for (int j = 0; j < logInfo.length; j++) {
                    HSSFCell cell1 = row1.createCell(j);
                    cell1.setCellValue(logInfo[j]);
                    cell1.setCellStyle(passStyle);
                    if (i != 0) {
                        sheet.setColumnWidth(i, 30 * 256);
                    }
                }
            }else{
                for (int j = 0; j < logInfo.length; j++) {
                    HSSFCell cell1 = row1.createCell(j);
                    cell1.setCellValue(logInfo[j]);
                    cell1.setCellStyle(failStyle);
                    if (i != 0) {
                        sheet.setColumnWidth(i, 30 * 256);
                    }
                }
            }
        }
        try {
            //创建文件
            out=new FileOutputStream(file);
            workbook.write(out);
            out.flush();
            out.close();
            Log.i("xmltoExcel","Excel写入成功，文件存放路径为"+file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out!=null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    */
}
