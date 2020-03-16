/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.commons.directive;


public class DirectveKit {


    public static String replacePageNumber(String url, int pageNumber) {

        int dotIndexOf = url.lastIndexOf(".");
        int splitIndexOf = url.lastIndexOf("-");

        if (dotIndexOf < 0 & splitIndexOf < 0) {
            return url + "-" + pageNumber;
        }

        if (dotIndexOf < 0 && splitIndexOf > 0) {
            return url.substring(0, splitIndexOf) + "-" + pageNumber;
        }

        if (dotIndexOf > 0 && splitIndexOf < 0) {
            return url.substring(0, dotIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
        }

        //if (dotIndexOf > 0 && spitIndexOf >0){
        return url.substring(0, splitIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
    }

    public static void main(String[] args) {

        System.out.println(replacePageNumber("/aa/bb/all-1", 123));
        System.out.println(replacePageNumber("/aa/bb/cc", 123));
        System.out.println(replacePageNumber("/aa/bb/cc.html", 123));
        System.out.println(replacePageNumber("/aa/bb/cc-33-44.html", 123));
        System.out.println(replacePageNumber("/aa/bb/cc-333.html", 123));
        System.out.println(replacePageNumber("/aa/bb/cc-1.html", 123));
        System.out.println(replacePageNumber("/aa/bb/cc-31", 123));
        System.out.println(replacePageNumber("/aa/bb/cc-", 123));

    }

}
