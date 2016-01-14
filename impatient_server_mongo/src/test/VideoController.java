/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

/**
 * You will need to create one or more Spring controllers to fulfill the
 * requirements of the assignment. If you use this file, please rename it
 * to something other than "AnEmptyController"
 * 
 * 
 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
 |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
 \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
 \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
 \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
 \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|

 * 
 */

package org.magnum.mobilecloud.video.client;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.repository.VideoRepository;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController
{
	@Autowired
	private VideoRepository videorepo;

	public static final String SECURITY_PATH = "/oauth/token";
	
	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String VIDEO_SVC_PATH = "/video";
	
	public static final String VIDEO_RATING_PATH = "/video/{id}/rating";

	public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";

	public static final String VIDEO_DATA_PATH_2 = VIDEO_SVC_PATH + "/{id}/data2";

	@RequestMapping(value = SECURITY_PATH, method = RequestMethod.GET)
	public void login(Principal p)
	{
		System.out.println("inside login procedure");
	}
	
	
	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission('read')")
	public @ResponseBody Collection<Video> getVideoList()
	{
		System.out.println("Returning videolist...");
		return videorepo.getVideoList();
	}

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v, HttpServletResponse response)
	{
		System.out.println("Adding Video metadata...");
		return videorepo.addVideo(v);
	}

	@RequestMapping(value = VIDEO_RATING_PATH + "{stars}" , method = RequestMethod.POST)
	public @ResponseBody Video addStarToVideo(@PathVariable("id") long id, @PathVariable("stars") int userRating,
			HttpServletResponse response)
	{
		System.out.println("Rating Video... ");
		return videorepo.addStarToVideo(id, userRating);
	}

	
	@RequestMapping(value = VIDEO_RATING_PATH  , method = RequestMethod.POST)
	public @ResponseBody Video getVideoRating(@PathVariable("id") long id,
			HttpServletResponse response)
	{
		System.out.println("Getting Videorating... ");
		return videorepo.getVideoRating(id);
	}

	
	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(@PathVariable("id") long id,
			@RequestParam("data") MultipartFile videoData, HttpServletResponse response)
	{
		System.out.println("Uploading Video data");
		return videorepo.uploadVideoData(id, videoData);
	}

	@RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.GET)
	public void getVideoData(@PathVariable("id") Long id, HttpServletResponse response)
	{
		System.out.println("Downloading Videodata");
		videorepo.getVideoData(id, response);
		return;
	}

	@RequestMapping(value = VIDEO_DATA_PATH_2, method = RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id)
	{
		System.out.println("Retrieving Video metadata");
		return videorepo.getVideoById(id);
	}

}
