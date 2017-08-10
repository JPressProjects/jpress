/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.template;

public class Thumbnail {

	private String name;
	private String size;
	private int width;
	private int height;

	public Thumbnail() {

	}

	public Thumbnail(String name, String size) {
		this.name = name;
		this.size = size;

		if (size != null && size.contains("x")) {
			String[] ss = size.split("x");
			if (2 == ss.length) {
				this.width = Integer.parseInt(ss[0].trim());
				this.height = Integer.parseInt(ss[1].trim());
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;

		if (size != null && size.contains("x")) {
			String[] ss = size.split("x");
			if (2 == ss.length) {
				this.width = Integer.parseInt(ss[0].trim());
				this.height = Integer.parseInt(ss[1].trim());
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getSizeAsString() {
		return width + "x" + height;
	}
	
	public String getUrl(String src){
		int index = src.lastIndexOf(".");
		return  src.substring(0, index) + "_"+getSizeAsString() + src.substring(index, src.length());
	}

}
