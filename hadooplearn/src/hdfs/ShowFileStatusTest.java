package hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.el.LessThanOrEqualsOperator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matcher.*;

public class ShowFileStatusTest {
	private MiniDFSCluster cluster; //use an in-process HDFS cluster for testing
	private FileSystem fs;
	
	@Before
	public void setUp() throws IOException{
		Configuration conf=new Configuration();
		if (System.getProperty("test.build.data")==null) {
			System.setProperty("test.build.data", "/tmp");
		}
		cluster =new MiniDFSCluster(conf,1,true,null);
		
		fs=cluster.getFileSystem();
		
		OutputStream out=fs.create(new Path("/dir/file"));
		out.write("content".getBytes("UTF-8"));
		out.close();
	}
	
	@After
	public void tearDown() throws IOException{
		if (fs!=null) {
			fs.close();
		}
		if (cluster!=null) {
			cluster.shutdown();
		}
	}
	
	@Test(expected=FileNotFoundException.class)
	public void throwsFileNotFoundForNonExistentFile() throws IOException{
		fs.getFileStatus(new Path("no-such-file"));
	}
	
	@Test
	public void fileStatusForFile() throws IOException{
		Path file=new Path("/dir/file");
		FileStatus stat=fs.getFileStatus(file);
		
		assertThat(stat.getPath().toUri().getPath(), is("/dir/file"));
		System.out.println(stat.getPath().toUri().getPath());
		System.out.println(stat.isDirectory());
		assertThat(stat.isDirectory(), is(false));
		assertThat(stat.getLen(), is(7l));
		assertTrue( System.currentTimeMillis()>=stat.getModificationTime());
		assertThat(stat.getReplication(), is((short)1));
		assertThat(stat.getBlockSize(), is((64*1024*1024l)));
		assertThat(stat.getOwner(), is("Lucifer"));
		assertThat(stat.getGroup(), is("supergroup"));
		assertThat(stat.getPermission().toString(), is("rw-r--r--"));
	}
	
	@Test
	public void fileStatusForDirectory() throws IOException{
		Path dir=new Path("/dir");
		FileStatus stat=fs.getFileStatus(dir);
		assertThat(stat.getPath().toUri().getPath(), is("/dir"));
		
		assertTrue(stat.isDirectory());
		assertEquals(stat.getLen(), 0l);
		assertTrue(System.currentTimeMillis()>=stat.getModificationTime());
		
		assertEquals(stat.getReplication(), 0);
		
		assertEquals(stat.getBlockSize(), 0);
		assertEquals(stat.getOwner(), "Lucifer");
		
		assertThat(stat.getGroup(), is("supergroup"));
		assertThat(stat.getPermission().toString(), is("rwxr-xr-x"));
	}
}
