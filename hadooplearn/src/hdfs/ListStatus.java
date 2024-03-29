package hdfs;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

public class ListStatus {
	public static void main(String[] args) throws IOException {
		String uri=args[0];
		Configuration conf=new Configuration();
		FileSystem fs=FileSystem.get(URI.create(uri),conf);
		
		Path[] paths=new Path[args.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i]=new Path(args[i]);
		}
		
		FileStatus[] status=fs.listStatus(paths);
		for (int i = 0; i < status.length; i++) {
			System.out.println(status[i].getPath());
		}
		System.out.println("==============");
		Path[] listedpPaths=FileUtil.stat2Paths(status);
		for (Path path : listedpPaths) {
			System.out.println(path);
		}
	}
}
