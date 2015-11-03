/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package pl.edu.agh.icsr.janet.reflect;

import pl.edu.agh.icsr.janet.*;
import java.util.*;
import pl.edu.agh.icsr.janet.yytree.YYNativeMethodImplementation;
import pl.edu.agh.icsr.janet.yytree.YYVariableDeclarator;

public interface INativeMethodInfo extends IMethodInfo {
    public String getNativeLanguage();
    public YYVariableDeclarator[] getParameters() throws CompileException;
    public Collection getUsedClassIdxs();
    public Collection getUsedFieldsIdxs();
    public Collection getUsedMethodsIdxs();
    public Collection getUsedStringsIdxs();
    public YYNativeMethodImplementation getImplementation();
}
