package sw4j.task.load;

import static org.junit.Assert.fail;

import org.junit.Test;

import sw4j.task.load.DataTaskLoadFile;

public class ToolLoaderFileTest {
	@Test
	public void test_file_load() {
		String [][] aryFiles = new String [][]{
				{"files/parse_test/swa_1207509187343_8783_index.n3","516"},	
				{"files/parse_test/demo.pdf","150200"},	
				//{"file:/C:/dl/workspace/svn-code-iw2.5/swutil/files/parse_test/swa_1207509187343_8783_index.n3","516"},	
				//{"file://C:/dl/workspace/svn-code-iw2.5/swutil/files/parse_test/swa_1207509187343_8783_index.n3","516"},	
				//{"file:///C:/dl/workspace/svn-code-iw2.5/swutil/files/parse_test/swa_1207509187343_8783_index.n3","516"},	
				{"/files/parse_test/swa_1207509187343_8783_index.n3","-1"},	
		};
		for (int i=0; i< aryFiles.length; i++){
			String szFile = aryFiles[i][0];
			DataTaskLoadFile task = (DataTaskLoadFile)   TaskLoad.load(szFile, null);
			if (!aryFiles[i][1].equals(""+task.getFileLength())){
				System.out.println(szFile);
				System.out.println("expected: " + aryFiles[i][1]);
				System.out.println("found: " + task.getFileLength());
				fail();
			}
			
		}
	}

}
