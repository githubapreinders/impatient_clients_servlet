package afr.iterson.impatient.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;

public class BannerLoader
{
	private Resource banner;
	
	public void setBanner(Resource banner)
	{
		this.banner = banner;
	}
	
	@PostConstruct
	public void showBanner() throws IOException
	{
		InputStream in = banner.getInputStream();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				break;
			System.out.println(line);
		}
		reader.close();
	}
	
	
	
	
}
