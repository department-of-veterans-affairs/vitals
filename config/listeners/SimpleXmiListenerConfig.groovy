import gov.va.vinci.leo.listener.SimpleXmiListener

String xmiPath = "c:\\my-dir\\${new Date().getTime()}";

File xmiPathFile = new File(xmiPath);
if (!xmiPathFile.exists())
    xmiPathFile.mkdirs();

Boolean openViewer = false;

listener = new SimpleXmiListener(xmiPathFile, openViewer);

String[] annotationsToOutput = [] as String[];

if (annotationsToOutput) {
    listener.setAnnotationTypeFilter(annotationsToOutput);
}