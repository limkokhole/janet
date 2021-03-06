/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package pl.edu.agh.icsr.janet.yytree;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pl.edu.agh.icsr.janet.CompileException;
import pl.edu.agh.icsr.janet.IJavaContext;
import pl.edu.agh.icsr.janet.ILocationContext;
import pl.edu.agh.icsr.janet.ParseException;
import pl.edu.agh.icsr.janet.Writer;
import pl.edu.agh.icsr.janet.reflect.ClassManager;
import pl.edu.agh.icsr.janet.reflect.IClassInfo;
import pl.edu.agh.icsr.janet.reflect.IMethodInfo;
import pl.edu.agh.icsr.janet.tree.Node;

public class YYMethod extends YYNode implements IMethodInfo, IScope {

    public static final int METHOD      = 1;
    public static final int CONSTRUCTOR = 2;

    public static final int METHOD_MODIFIERS =
        YYModifierList.ACCESS_MODIFIERS | Modifier.ABSTRACT | Modifier.STATIC |
        Modifier.FINAL | Modifier.SYNCHRONIZED | Modifier.NATIVE;

    public static final int CONSTRUCTOR_MODIFIERS =
        YYModifierList.ACCESS_MODIFIERS;

    public static final int INTERFACE_METHOD_MODIFIERS =
        Modifier.PUBLIC | Modifier.ABSTRACT;

    public static final int NONABSTRACT_METHOD_MODIFIERS =
        Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE |
        Modifier.SYNCHRONIZED;

    YYClass cls;
    int type; // method or constructor

    int modifiers;
    IClassInfo returnType;
    int rettypedims;
    String name;
    YYStatement body;
    transient HashMap<String, IClassInfo> throwlist;
    transient YYVariableDeclarator[] parameters;
    transient IClassInfo[] paramtypes;
    transient String argsignature;
    transient String signature;
    transient String jnisignature;

    YYType unresolvedReturnType;
    YYVariableDeclaratorList unresolvedParameters;
    YYTypeList unresolvedThrows;

    public YYMethod(IJavaContext cxt, String name, int type)
            throws CompileException {
        super(cxt);
        cls = (YYClass)cxt.getScope();
        this.name = name;
        this.type = type;

        if (cls.isInterface()) {
            this.modifiers = Modifier.PUBLIC + Modifier.ABSTRACT;
        } else {
            this.modifiers = 0;
        }
    }

    // called for constructors only
    public YYMethod checkName(ILocationContext cxt) throws CompileException {
        if (!cls.getSimpleName().equals(name)) {
            cxt.reportError("Invalid method declaration; return type required");
            this.type = METHOD;
        }
        return this;
    }

    public YYMethod setModifiers(YYModifierList m) throws CompileException {
        int errm;
        int modifiers = m.modifiers;
        String stype;
        ensureUnlocked();
        if (cls.isInterface()) { // always public abstract
            errm = modifiers & ~INTERFACE_METHOD_MODIFIERS;
            stype = "Interface methods";
        } else if (isConstructor()) {
            errm = modifiers & ~CONSTRUCTOR_MODIFIERS;
            stype = "Constructors";
        } else {
            if (Modifier.isPrivate(modifiers)) modifiers |= Modifier.FINAL;
            errm = modifiers & ~METHOD_MODIFIERS;
            stype = "Methods";
            if (errm == 0 && Modifier.isAbstract(modifiers)) {
                errm = modifiers & NONABSTRACT_METHOD_MODIFIERS;
                stype = "Abstract methods";
            }
        }
        if (errm != 0) {
            YYNode n = m.findFirst(errm);
            n.reportError(stype + " can't be " + Modifier.toString(errm));
        }
        this.modifiers |= (modifiers & ~errm);
        return this;
    }

    public int getScopeType() {
        if (isConstructor()) return IScope.CONSTRUCTOR;
        if (Modifier.isStatic(modifiers)) {
            return IScope.STATIC_METHOD;
        } else {
            return IScope.INSTANCE_METHOD;
        }
    }

    public IScope getEnclosingUnit() {
        return cls;
    }

    public YYMethod setBody(YYStatement body) throws CompileException {
        ensureUnlocked();
        if (body == null) {
            if ((modifiers & (Modifier.ABSTRACT + Modifier.NATIVE)) == 0) {
                reportError(this + " requires a method body. " +
                    "Otherwise declare it as abstract");
            }
        } else {
            if (Modifier.isAbstract(modifiers)) {
                body.reportError("Abstract methods can't have a body");
            } else {
                if (!body.isPure()) {
                    this.body = body;
                    super.append(body);
                }
            }
        }
        return this;
    }

    public YYMethod setReturnType(YYType type) {
        return setReturnType(type, 0);
    }

    public YYMethod setReturnType(YYType rettype, int rettypedims) {
        ensureUnlocked();
        this.unresolvedReturnType = rettype;
        this.rettypedims = rettypedims;
        return this;
    }

    public YYMethod setThrows(YYTypeList throwlist) {
        ensureUnlocked();
        unresolvedThrows = throwlist;
        return this;
    }

    public YYMethod setParameters(YYVariableDeclaratorList params) {
        ensureUnlocked();
        unresolvedParameters = params;
        return this;
    }

    public IClassInfo getDeclaringClass() {
        return cls;
    }

    public String getName() {
        if (isConstructor()) return "<init>";
        return this.name;
    }

    public int getModifiers() {
        return modifiers;
    }

    private IClassInfo resolveReturnType() throws ParseException {
        if (isConstructor()) {
            return returnType = cls.getClassManager().VOID;
        }
        IClassInfo resolved = unresolvedReturnType.getResolvedType();
        returnType = (rettypedims == 0) ? resolved
                                        : resolved.getArrayType(rettypedims);
        //rettypedims = 0;
        //unresolvedReturnType = null;
        return returnType;
    }

    public IClassInfo getReturnType() throws ParseException {
        if (returnType != null) return returnType;
        lock();
        return returnType = resolveReturnType();
    }

    public boolean isConstructor() {
        return this.type == CONSTRUCTOR;
    }

    public String getArgumentSignature() throws ParseException {
        if (argsignature != null) return argsignature;
        lock();
        String s = "";
        if (unresolvedParameters != null) {
            for (Node node : unresolvedParameters) {
                s += ((YYVariableDeclarator)node).getType().getSignature();
            }
        }
        return argsignature = s;
    }

    public String getJLSSignature() throws ParseException {
        if (signature != null) return signature;
        return signature = "(" + getArgumentSignature() + ")";
    }

    public String getJNISignature() throws ParseException {
        if (jnisignature != null) return jnisignature;
        return jnisignature = getJLSSignature() +
            getReturnType().getSignature();
    }

    public Map<String, IClassInfo> getExceptionTypes() throws ParseException { // JLS 8.4.4
        if (throwlist != null) return throwlist;
        lock();
        throwlist = new HashMap<String, IClassInfo>();
        if (unresolvedThrows != null) {
            ClassManager cm = cls.getClassManager();
            for(Node node : unresolvedThrows) {
                YYType t = (YYType)node;
                IClassInfo rest = t.getResolvedType();
                String name = rest.getFullName();
                if (!rest.isAssignableFrom(cm.Throwable)) {
                    t.reportError(rest.toString() + " in throws clause " +
                        "must be subclass of java.lang.Throwable");
                } else { // repetitions ARE permitted!
                    throwlist.put(name, rest);
                }
            }
        }
        return throwlist;
    }

    public YYVariableDeclarator[] getParameters() {
        if (parameters != null) return parameters;
        lock();
        if (unresolvedParameters == null) {
            return parameters = new YYVariableDeclarator[0];
        }
        int len = unresolvedParameters.countSons();
        parameters = new YYVariableDeclarator[len];
        Iterator<Node> i;
        int idx;
        for (i = unresolvedParameters.iterator(), idx=0; i.hasNext(); idx++) {
            parameters[idx] = (YYVariableDeclarator)i.next();
        }
        return parameters;
    }

    public IClassInfo[] getParameterTypes() {
        if (paramtypes != null) return paramtypes;
        lock();
        YYVariableDeclarator[] params = getParameters();
        paramtypes = new IClassInfo[params.length];
        try {
            for (int i=0; i<params.length; i++) {
                paramtypes[i] = params[i].getType();
            }
        } catch (ParseException e) {
            throw new RuntimeException();
        }
        return paramtypes;
    }

    public IScope getEnclosingScope() { return cls; }
    public YYClass getCurrentClass() { return cls; }
    public IScope getCurrentMember() { return this; }

    public void resolve() throws ParseException {
        lock();
        if (body == null) return;
        body.resolve();
        // checking the exceptions
        Collection<IClassInfo> marked = getExceptionTypes().values();
        ClassManager classMgr = cls.getClassManager();
        for (Map.Entry<IClassInfo, YYStatement> entry : body.getExceptionsThrown().entrySet()) {
            IClassInfo exc = entry.getKey();
            if (classMgr.isUncheckedException(exc)) {
                continue;
            }
            if (!ClassManager.containsException(marked, exc)) {
                YYStatement origin = entry.getValue();
                origin.reportError("Exception " + exc.getFullName() +
                    " must be caught, or it must be declared in the throws" +
                    " clause of " + this);
            }
        }
    }

    public void write(Writer w) throws java.io.IOException {
        super.write(w);
    }

    public String toString() {
        try {
            return (isConstructor()
                       ? "constructor " + name
                       : "method " + getReturnType().getFullName() + " " +
                           getName()) +
                "(" + cls.classMgr.getTypeNames(getParameterTypes()) + ")";
        } catch (ParseException e) { throw new IllegalStateException(); }
    }

    public String describe() {
        try {
            String s = getModifiers() + " " +
                getReturnType().getFullName() + " " +
                getName() +
                "(" + (unresolvedParameters == null ? "" : unresolvedParameters.toString()) +
                ")" +
                " _" + getJNISignature() + "_";
            Iterator<IClassInfo> i = getExceptionTypes().values().iterator();
            if (i.hasNext()) {
                s += " throws ";
                while (i.hasNext()) {
                    s += i.next();
                    if (i.hasNext()) s+= ", ";
                }
            }
            return s;
        } catch (ParseException e) {
            return "exception has occured";
        }
    }

    public void lock() {
        if (!isLocked()) {
            cls.lock();
            super.lock();
        }
    }
}