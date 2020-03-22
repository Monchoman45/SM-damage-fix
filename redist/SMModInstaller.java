import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;

public class SMModInstaller {
	public static int BUF_SIZE = 4096;
	public static boolean PRINT = false;

	public static void main(String[] args) throws IOException {
		if(args.length < 3) {
			System.err.println("Usage: java -jar smmi.jar path/to/StarMade.jar path/to/output path/to/folder_of_replacement_classes/");
			assert false;
		}
		PRINT = true;
		install(args[0], args[1], args[2]);
	}

	public static void install(String source, String dest, String replace) throws IOException {
		if(!replace.endsWith("/")) {replace += '/';}
		HashMap<String, Boolean> classes = new HashMap<String, Boolean>();
		walk(classes, "", replace);

		ZipInputStream in = new ZipInputStream(new FileInputStream(source));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest));
		byte[] buf = new byte[BUF_SIZE];
		for(ZipEntry ze = in.getNextEntry(); ze != null; ze = in.getNextEntry()) {
			if(ze.isDirectory()) {out.putNextEntry(ze);}
			else if(classes.containsKey(ze.getName())) {
				out.putNextEntry(new ZipEntry(ze.getName()));
				if(PRINT) {System.out.println("-> " + ze.getName());}
				FileInputStream f = new FileInputStream(replace + ze.getName());
				for(int len = f.read(buf); len > 0; len = f.read(buf)) {out.write(buf, 0, len);}
				classes.put(ze.getName(), true);
			}
			else {
				out.putNextEntry(ze);
				for(int len = in.read(buf); len > 0; len = in.read(buf)) {out.write(buf, 0, len);}
			}
		}

		for(String file : classes.keySet()) {
			if(!classes.get(file)) {
				out.putNextEntry(new ZipEntry(file));
				FileInputStream f = new FileInputStream(replace + file);
				for(int len = f.read(buf); len > 0; len = f.read(buf)) {out.write(buf, 0, len);}
			}
		}
		in.close();
		out.close();
	}

	private static void walk(HashMap<String, Boolean> classes, String path, String base) {
		File[] files = (new File(base + path)).listFiles();
		if(files == null) {return;}
		for(File f : files) {
			if(f.isDirectory()) {walk(classes, path + f.getName() + "/", base);}
			else {classes.put(path + f.getName(), false);}
		}
	}
}
