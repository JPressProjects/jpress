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
package io.jpress.core.addon;

public abstract class Addon {

	private ThreadLocal<Boolean> tl = new ThreadLocal<Boolean>();

	private final Hooks hooks;

	public Addon() {
		hooks = new Hooks(this);
	}

	public Hooks getHooks() {
		return hooks;
	}

	protected void nextInvoke() {
		tl.set(true);
	}

	/**
	 * 子类不要调用此方法
	 */
	public void hookInvokeFinished() {
		tl.remove();
	}

	/**
	 * 子类不要调用此方法
	 */
	public boolean letNextHookInvoke() {
		return tl.get() != null && tl.get() == true;
	}

	public abstract boolean onStart();

	public abstract boolean onStop();

}
