/**
 * This file is part of LWC (https://github.com/Hidendra/LWC)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nidefawl.Stats.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.nidefawl.Stats.Stats;

public class UpdaterFile {

	/**
	 * The remote url location
	 */
	private String remoteLocation;

	/**
	 * The local url location
	 */
	private String localLocation;
	private double localVersion;
	private double remoteVersion;

	public UpdaterFile(String remoteLocation, String localLocation, double localVersion, double remoteVersion) {
		this.remoteLocation = remoteLocation;
		this.localLocation = localLocation;
		this.localVersion = localVersion;
		this.remoteVersion = remoteVersion;
	}

	/**
	 * @return the local file location
	 */
	public String getLocalLocation() {
		return localLocation;
	}

	/**
	 * @return the remote url location
	 */
	public String getRemoteLocation() {
		return remoteLocation;
	}

	/**
	 * Set the local file location
	 * 
	 * @param localLocation
	 */
	public void setLocalLocation(String localLocation) {
		this.localLocation = localLocation;
	}

	/**
	 * Set the remote url location
	 * 
	 * @param remoteLocation
	 */
	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}

	private void saveTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;

		while ((len = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}
	}

	public boolean update(boolean autoUpdate) throws Exception {
		if (remoteVersion > localVersion) {
			if (autoUpdate) {
				try {
					Stats.LogInfo("Newer version of "+localLocation+" found. local: "+localVersion+" remote: "+remoteVersion);
					Stats.LogInfo("Downloading file : " + remoteLocation);
					URL url = new URL(remoteLocation);
					File file = new File(localLocation);
					file.mkdirs();
					if (file.exists()) {
						file.delete();
					}
					InputStream inputStream = url.openStream();
					OutputStream outputStream = new FileOutputStream(file);
					saveTo(inputStream, outputStream);
					inputStream.close();
					outputStream.close();
					Stats.LogInfo("Download complete. File saved to "+file.getAbsolutePath());
					return true;
				} catch (Exception e) {
					Stats.LogInfo("Download failed: " + e.getMessage());
					e.printStackTrace();
					return false;
				}
			} else {
				Stats.LogInfo("There is an update for " + localLocation);
				return false;
			}
		}
		return false;
	}

}
