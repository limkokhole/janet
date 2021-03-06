/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package pl.edu.agh.icsr.janet.yytree;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import pl.edu.agh.icsr.janet.CompileException;
import pl.edu.agh.icsr.janet.IJavaContext;
import pl.edu.agh.icsr.janet.ParseException;
import pl.edu.agh.icsr.janet.Writer;
import pl.edu.agh.icsr.janet.reflect.ClassManager;
import pl.edu.agh.icsr.janet.reflect.DefaultConstructor;
import pl.edu.agh.icsr.janet.reflect.IClassInfo;
import pl.edu.agh.icsr.janet.reflect.IFieldInfo;
import pl.edu.agh.icsr.janet.reflect.IMethodInfo;
import pl.edu.agh.icsr.janet.tree.Node;

public class YYClass extends YYNode implements IClassInfo, IScope {

    public static final int CLASS     = 1;
    public static final int INTERFACE = 2;

    public static final int CLASS_MODIFIERS =
        Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.FINAL;
// TODO: fix
    public static final int INNER_CLASS_MODIFIERS =
        CLASS_MODIFIERS | Modifier.STATIC | YYModifierList.ACCESS_MODIFIERS;

    public static final int INTERFACE_MODIFIERS =
        Modifier.PUBLIC | Modifier.ABSTRACT;
// TODO: fix
    public static final int INNER_INTERFACE_MODIFIERS =
        Modifier.ABSTRACT | YYModifierList.ACCESS_MODIFIERS;

    public void setLoadLibrary(boolean loadLibrary) {
        this.loadLibrary = loadLibrary;
    }

    String getLoadLibraryHeader() {
        if (loadLibrary) {
            return "System.loadLibrary(\""
                + getLibName()
                + "\");";
        } else {
            return "/* --noloadlibrary */";
        }
    }

    String janetHeader =
        "\n" +
        "////// BEGIN OF GENERATED CODE //////\n" +
        "\n" +
        "%INDENT%private static native void janetClassInit$();\n" +
        "%INDENT%private static native void janetClassFinalize$();\n" +
        "\n" +
        "%INDENT%private static class janet$ {\n" +
        "%INDENT%    janet$() {\n" +
        "%INDENT%        %LOADLIBRARY%\n" +
        "%INDENT%        %CLASSNAME%.janetClassInit$();\n" +
        "%INDENT%    }\n" +
        "%INDENT%    public void finalize() { " +
            "%CLASSNAME%.janetClassFinalize$(); }\n" +
        "%INDENT%}\n" +
        "%INDENT%static janet$ janet$ = new janet$();\n" +
        "\n" +
        "////// END OF GENERATED CODE //////\n" +
        "\n";

    ClassManager classMgr;

    int type; // CLASS or INTERFACE
    int modifiers;
    String name;
    transient String signature;
    IClassInfo superclass;
    IScope enclosing; // compilation unit, class, or statement
    Map<String, YYVariableDeclarator> dclfields;
    Vector<YYNativeStatement> implicitNativeMethods;

    transient SortedMap<String, IFieldInfo> accfields;
    transient SortedMap<String, YYMethod> dclmethods;
    transient SortedMap<String, IMethodInfo> accmethods;
    transient Map<String, IMethodInfo> constructors;
    transient Map<String, IClassInfo> interfaces;
    transient Map<String, IClassInfo> assignableClasses;

    private YYType unresolvedSuperclass;
    private YYTypeList unresolvedInterfaces;
    private Vector<YYMethod> unresolvedMethods;

    private boolean workingFlag;

    // maps signatures to indexes in appropriate vectors
    Map<String, Integer> referencedClassesIdx;
    Map<String, Integer> referencedFieldsIdx;
    Map<String, Integer> referencedMethodsIdx;
    Map<String, Integer> referencedStringLiteralsIdx;

    Vector<IClassInfo> referencedClasses;
    Vector<IFieldInfo> referencedFields;
    Vector<Integer> referencedFieldsClsIdxs;
    Vector<IMethodInfo> referencedMethods;
    Vector<Integer> referencedMethodsClsIdxs;
    Vector<String> referencedStringLiterals;
//    boolean valid;

    private boolean hasNativeMethodImpls = false;
    private String libName;
    private boolean loadLibrary;

    public YYClass(IJavaContext cxt, String name, int type, YYModifierList m)
           throws CompileException {
        super(cxt);
//        valid = true;
        this.classMgr = cxt.getClassManager();
        this.name = name;
        this.enclosing = cxt.getScope();
        this.type = type;
        this.modifiers = checkModifiers(m);
        this.dclfields = new HashMap<String, YYVariableDeclarator>();
        this.implicitNativeMethods = new Vector<YYNativeStatement>();
        this.unresolvedMethods = new Vector<YYMethod>();

        this.referencedClassesIdx = new HashMap<String, Integer>();
        this.referencedFieldsIdx = new HashMap<String, Integer>();
        this.referencedMethodsIdx = new HashMap<String, Integer>();
        this.referencedStringLiteralsIdx = new HashMap<String, Integer>();

        this.referencedClasses = new Vector<IClassInfo>();
        this.referencedFields = new Vector<IFieldInfo>();
        this.referencedFieldsClsIdxs = new Vector<Integer>();
        this.referencedMethods = new Vector<IMethodInfo>();
        this.referencedMethodsClsIdxs = new Vector<Integer>();
        this.referencedStringLiterals = new Vector<String>();
    }

    // inner classes (unsupported)
    public YYClass(IJavaContext cxt, YYType type) {
        super(cxt);
        this.classMgr = cxt.getClassManager();
        this.enclosing = cxt.getScope();
        this.dclfields = new HashMap<String, YYVariableDeclarator>();
        this.implicitNativeMethods = new Vector<YYNativeStatement>();
        this.unresolvedMethods = new Vector<YYMethod>();
    }

    // inner classes (unsupported)
    public YYClass(IJavaContext cxt, YYExpression e, String s) {
        super(cxt);
        this.classMgr = cxt.getClassManager();
        this.enclosing = cxt.getScope();
        this.dclfields = new HashMap<String, YYVariableDeclarator>();
        this.implicitNativeMethods = new Vector<YYNativeStatement>();
        this.unresolvedMethods = new Vector<YYMethod>();
    }

    private int checkModifiers(YYModifierList m) throws CompileException {
        int modifiers = (m != null) ? m.getModifiers() : 0;
        boolean inner = false;
        if (enclosing != null) {
            int etype = enclosing.getScopeType();
            if (etype == IScope.STATIC_CLASS ||
                    etype == IScope.INSTANCE_CLASS) {
                inner = true;
            }
        }
        int errm;
        boolean innerok;
        String stype1, stype2;
        if (isInterface()) {
            modifiers |= Modifier.ABSTRACT; // Interfaces are always abstract
            errm = modifiers & ~(inner ? INNER_INTERFACE_MODIFIERS
                                       : INTERFACE_MODIFIERS);
            innerok = (modifiers & ~INNER_INTERFACE_MODIFIERS) == 0;
            stype1 = "interfaces"; stype2 = "Interfaces";
        } else {
            errm = modifiers & ~(inner ? INNER_CLASS_MODIFIERS
                                       : CLASS_MODIFIERS);
            innerok = (modifiers & ~INNER_CLASS_MODIFIERS) == 0;
            stype1 = "classes"; stype2 = "Classes";
        }
        if (errm != 0) {
            YYNode n = m.findFirst(errm);
            if (innerok) {
                n.reportError("Only inner " + stype1 + " can be " +
                    Modifier.toString(errm));
            } else {
                n.reportError(stype2 + " can't be " +
                    Modifier.toString(errm));
            }
            return modifiers &= ~errm;
        }
        return modifiers;
    }

    public YYClass setSuperclass(YYType superclass) {
        ensureUnlocked();
        this.unresolvedSuperclass = superclass;
        return this;
    }

    public YYClass setInterfaces(YYTypeList interfaces) {
        ensureUnlocked();
        this.unresolvedInterfaces = interfaces;
        return this;
    }

    public YYClass addStaticInitializer(YYStatement static_initializer) {
        ensureUnlocked();
        static_initializer.setScopeType(IScope.STATIC_INITIALIZER);
        super.append(static_initializer);
        return this;
    }

    public YYClass addConstructor(YYMethod constructor) {
        ensureUnlocked();
        if (!constructor.isConstructor()) {
            throw new IllegalArgumentException();
        }
        return addMethod(constructor);
    }

    public YYClass addInstanceInitializer(YYStatement initializer) {
        ensureUnlocked();
        initializer.setScopeType(IScope.INSTANCE_INITIALIZER);
        super.append(initializer);
        return this;
    }

    public YYClass addStaticNativeStatement(YYStaticNativeStatement stmt) {
        super.append(stmt);
        return this;
    }

    public YYClass addField(YYField flds) throws CompileException {
        ensureUnlocked();
        super.append(flds);
        for (Node node : flds) {
            YYVariableDeclarator fld = (YYVariableDeclarator)node;
            if (dclfields.containsKey(fld.getName())) {
                fld.reportError("Duplicate field declaration: " +
                    fld.getName() + " already declared at line " +
                    dclfields.get(fld.getName()).
                        lbeg().getLine());
            }
            dclfields.put(fld.getName(), fld);
        }
        return this;
    }

    public YYClass addMethod(YYMethod method) {
        ensureUnlocked();
        unresolvedMethods.add(method);
        super.append(method);
        return this;
    }

    public void addNativeMethodImplementation() {
        hasNativeMethodImpls = true;
    }

    private void resolveMethodsAndConstructors() throws ParseException {
        dclmethods = new TreeMap<String, YYMethod>();
        constructors = new HashMap<String, IMethodInfo>();
        for (Iterator<YYMethod> i = unresolvedMethods.iterator(); i.hasNext();) {
            YYMethod mth = i.next();
            if (mth.isConstructor()) {
                String key = mth.getJLSSignature();
                IMethodInfo other = constructors.get(key);
                if (other != null) { // JLS 8.6.2
                    mth.reportError("Duplicate constructor declaration: " +
                        mth.toString());
                } else {
                    constructors.put(key, mth);
                }
            } else { // method
                String key = mth.getName() + mth.getJLSSignature();
                IMethodInfo other = dclmethods.get(key);
                if (other != null) { // JLS 8.4.2
                    if (classMgr.equals(mth.getReturnType(),
                                        other.getReturnType())) {
                        mth.reportError("Duplicate method declaration: " +
                            mth.toString());
                    } else {
                        mth.reportError("Methods can't be redefined " +
                            "with a different return type: " +
                            mth.toString() + " was " + other.toString());
                    }
                } else {
                    dclmethods.put(key, mth);
                }
            }
        }
        // add default constructor if neccessary (JLS 8.6.7)
        if (constructors.isEmpty()) {
            constructors.put("()", new DefaultConstructor(this));
        }
        unresolvedMethods = null;
    }

    public YYClass addClass(YYClass cls) { // not yet supported
        ensureUnlocked();
        super.append(cls);
        return this;
    }


    public boolean isInterface() {
        return (type == INTERFACE);
    }

    public boolean isArray() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    public boolean isReference() {
        return true;
    }

    public boolean isAccessibleTo(String pkg) {
        lock();
        if (Modifier.isPublic(modifiers)) {
            return true;
        }
        if (enclosing instanceof YYCompilationUnit) {
            String pname = ((YYCompilationUnit)enclosing).getPackageName();
            return (pkg != null ? pkg : "").equals(pname);
        } else {
            throw new IllegalStateException("Inner classes are not supported");
        }
    }

    public String getPackageName() throws CompileException {
        if (enclosing instanceof YYCompilationUnit) {
            return ((YYCompilationUnit)enclosing).getPackageName();
        } else {
            return getDeclaringClass().getPackageName();
        }
    }

    public String getSimpleName() {
        return name;
    }

    public String getFullName() {
        if (enclosing instanceof YYCompilationUnit) {
            return ClassManager.getQualifiedName(((YYCompilationUnit)enclosing).
                getPackageName(), name);
        } else if (enclosing instanceof YYClass) {
            return ((YYClass)enclosing).getFullName() + "." + name;
        } else {
            throw new IllegalStateException("Local classes do not have fully " +
                "qualified names");
        }
    }

    public String getJNIName() {
        if (enclosing instanceof YYCompilationUnit) {
            String result = ((YYCompilationUnit)enclosing).getPackageName().
                replace('.', '/');
            if (!result.equals("")) result += "/";
            result += name;
            return result;
        } else if (enclosing instanceof YYClass) {
            return ((YYClass)enclosing).getJNIName() + "$" + name;
        } else {
            throw new IllegalStateException("Local classes not supported");
        }
    }

    public String getJNIType() {
        try {
            if (this.isAssignableFrom(classMgr.Throwable)) return "jthrowable";
            return "jobject";
        } catch (ParseException e) {
            throw new IllegalStateException();
        }
    }

    /*
            if (enclosing instanceof YYClass) {
            return ((YYClass)enclosing).getFullName() + "$" + name;
        } else { // anonymous
            return "nested inner class";
        }*/

    public int getModifiers() {
        return modifiers;
    }

    public IClassInfo getDeclaringClass() {
        return enclosing.getCurrentClass();
    }

    public IClassInfo getSuperclass() throws ParseException {
        if (isInterface()) {
            throw new UnsupportedOperationException();
        }
        if (superclass == null) {
            lock();
            superclass = unresolvedSuperclass == null
                             ? classMgr.Object
                             : unresolvedSuperclass.getResolvedType();
        }
        return superclass;
    }

    public IClassInfo getArrayType() throws CompileException {
        return getArrayType(1);
    }

    public IClassInfo getArrayType(int dims) throws CompileException {
        if (dims < 1) {
            throw new IllegalArgumentException();
        }
        if (classMgr == null) { // not registered yet
            throw new IllegalStateException();
        }
        return classMgr.getArrayClass(this, dims);
    }

    public IClassInfo getComponentType() {
        return null;
    }

    public int getScopeType() {
        if (type == CLASS) {
            if (enclosing == null || Modifier.isStatic(modifiers)) {
                return IScope.STATIC_CLASS;
            } else {
                return IScope.INSTANCE_CLASS;
            }
        } else if (type == INTERFACE) {
            return IScope.INTERFACE;
        } else {
            throw new IllegalArgumentException();
        }
    }
/*
    public YYClass setExtends(YYTypeList extendslist) {
        this.interfaces = extendslist;
        return this;
    }
*/
    public String getSignature() {
        if (signature != null) return signature;
        return signature = "L" + getJNIName() + ";";
    }

    public Map<String, YYVariableDeclarator> getDeclaredFields() {
        return dclfields;
    }

    /**
     * Enforces deep fields resolving
     */
    public SortedMap<String, IFieldInfo> getAccessibleFields() throws ParseException {
        if (accfields != null) return accfields;
        lock();
        return accfields = classMgr.getAccessibleFields(this);
    }

    public SortedMap<String, ? extends IFieldInfo> getFields(String name) throws ParseException {
        return classMgr.getFields(this, name);
    }

    /**
     * Enforces method resolving
     */
    public SortedMap<String, YYMethod> getDeclaredMethods() throws ParseException {
        if (dclmethods != null) return dclmethods;
        lock();
        resolveMethodsAndConstructors();
        return dclmethods;
    }

    public SortedMap<String, IMethodInfo> getAccessibleMethods() throws ParseException {
        if (accmethods != null) return accmethods;
        return accmethods = classMgr.getAccessibleMethods(this);
    }

    public SortedMap<String, ? extends IMethodInfo> getMethods(String name) throws ParseException {
        return classMgr.getMethods(this, name);
    }

    public SortedMap<String, ? extends IMethodInfo> getMethods(String name, String jlssignature)
            throws ParseException {
        return classMgr.getMethods(this, name, jlssignature);
    }

    public Map<String, IMethodInfo> getConstructors() throws ParseException {
        if (constructors != null) return constructors;
        lock();
        resolveMethodsAndConstructors();
        return constructors;
    }

    /**
     * Enforces interfaces resolving
     */
    public Map<String, IClassInfo> getInterfaces() throws ParseException {
        if (interfaces != null) return interfaces;
        lock();
        interfaces = new HashMap<String, IClassInfo>();
        if (unresolvedInterfaces == null) return interfaces;
        for (Node node : unresolvedInterfaces) {
            YYType yyintf = ((YYType)node);
            IClassInfo newintf = yyintf.getResolvedType();
            String key = newintf.getFullName();
            IClassInfo oldintf = interfaces.get(key);
            if (oldintf != null) {
                yyintf.reportError("Interface " + key + " repeated");
            } else {
                interfaces.put(key, newintf);
            }
        }
        return interfaces;
    }

    public boolean isAssignableFrom(IClassInfo cls) throws ParseException { // JLS 5.1.4
        if (assignableClasses == null) {
            lock();
            assignableClasses = classMgr.getAssignableClasses(this);
        }
        return assignableClasses.containsKey(cls.getFullName());
    }

    public int isCastableTo(IClassInfo clsTo) throws ParseException {
        if (isAssignableFrom(clsTo)) {
            return CAST_CORRECT;
        }
        return classMgr.isCastableTo(this, clsTo) ? CAST_REQUIRES_RTCHECK
                                                  : CAST_INCORRECT;
    }

    /**
     * The nonarray class is a subclass of another class iff it is not
     * interface, it isassignable of that class and that class is not
     * interface
     * JLS 8.4.4
     */
    public boolean isSubclassOf(IClassInfo cls) throws ParseException {
        return !isInterface() && !cls.isInterface() && isAssignableFrom(cls);
    }

    public boolean equals(IClassInfo cls) throws CompileException {
        return this == cls; // unique as quaranteed by ClassManager
    }

    public final ClassManager getClassManager() {
        return classMgr;
    }

    public final void lock() {
        if (!isLocked()) {
            if (classMgr == null) throw new IllegalStateException();
            classMgr.lock();
            super.lock();
        }
    }

    public IScope getEnclosingScope() {
        return enclosing;
    }

    public YYClass getCurrentClass() {
        return this;
    }

    public IScope getCurrentMember() {
        if (enclosing instanceof YYClass) { // we are member class
            return this;
        } else {
            return enclosing.getCurrentMember();
        }
    }

    public void setWorkingFlag(boolean working) {
        workingFlag = working;
    }

    public boolean getWorkingFlag() {
        return workingFlag;
    }

    // todo: check for duplicate method definitions
    public void resolve() throws ParseException {
        // self-reference always first on the list
        addReferencedClass(this);
        super.resolve();
    }

    public Integer addReferencedClass(IClassInfo cls) {
        String key = cls.getJNIName();
        Integer old = referencedClassesIdx.get(key);
        if (old != null) {
            return old;
        }
        Integer pos = new Integer(referencedClasses.size());
        referencedClasses.add(cls);
        referencedClassesIdx.put(key, pos);
        return pos;
    }

    public Integer addReferencedMethod(int classidx, IMethodInfo mth)
            throws ParseException {
        String key = ClassManager.getMethodKey(mth);
        Integer old = referencedMethodsIdx.get(key);
        if (old != null) {
            return old;
        }
        Integer pos = new Integer(referencedMethods.size());
        referencedMethods.add(mth);
        referencedMethodsClsIdxs.add(new Integer(classidx));
        referencedMethodsIdx.put(key, pos);
        return pos;
    }

    public Integer addReferencedField(int classidx, IFieldInfo fld)
            throws CompileException {
        String key = ClassManager.getFieldKey(fld);
        Integer old = referencedFieldsIdx.get(key);
        if (old != null) {
            return old;
        }
        Integer pos = new Integer(referencedFields.size());
        referencedFields.add(fld);
        referencedFieldsClsIdxs.add(new Integer(classidx));
        referencedFieldsIdx.put(key, pos);
        return pos;
    }

    public Integer addReferencedStringLiteral(String lit) {
        String interlit = lit.intern();
        Integer old = referencedStringLiteralsIdx.get(interlit);
        if (old != null) {
            return old;
        }
        Integer pos = new Integer(referencedStringLiterals.size());
        referencedStringLiterals.add(interlit);
        referencedStringLiteralsIdx.put(interlit, pos);
        return pos;
    }

    public Vector<IClassInfo> getReferencedClasses() { return referencedClasses; }
    public Vector<IMethodInfo> getReferencedMethods() { return referencedMethods; }
    public Vector<Integer> getRefMethodClsIdxs() { return referencedMethodsClsIdxs; }
    public Vector<IFieldInfo> getReferencedFields() { return referencedFields; }
    public Vector<Integer> getRefFieldClsIdxs() { return referencedFieldsClsIdxs; }
    public Vector<String> getRefStringLiterals() { return referencedStringLiterals; }

    public int addImplicitNativeMethod(YYNativeStatement stmt) {
        int pos = implicitNativeMethods.size();
        implicitNativeMethods.add(stmt);
        addNativeMethodImplementation();
        return pos;
    }

    public String getLibName() {
        if (libName != null) return libName;
        try {
            String name = getPackageName();
            if (name.equals("")) name = getSimpleName();
            return (libName = ClassManager.mangle(name));
        } catch (CompileException e) {
            throw new RuntimeException();
        }
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public void write(Writer w) throws java.io.IOException {
        if (!hasNativeMethodImpls) {
            super.write(w);
            return;
        }

        w.getNativeWriter().classWriteInit(this);

        Writer.Substituter s = w.getSubstituter();
        String oldCls = s.setSubst("CLASSNAME", getSimpleName());
        String oldInd = s.setSubst("INDENT", Writer.makeIndent(this.beg_charno + 4));
        String oldLib = s.setSubst("LOADLIBRARY", getLoadLibraryHeader());

        int beg = beg_charno0;
        Iterator<Node> i = iterator();
        YYNode n;
        StringBuffer buf = ibuf().getbuf();

        //must have at least one son, as hasNativeMethodImpls is true
        n = (YYNode)i.next();
        int pos = n.beg_charno0;
        while (Character.isWhitespace(buf.charAt(pos-1))) pos--;
        pos++;
        w.write(buf.substring(beg, pos-1) + "\n");
        w.write(janetHeader, true);
        w.write(Writer.makeIndent(n.beg_charno));
        n.write(w);
        pos = n.end_charno0;

        while (i.hasNext()) {
            n = (YYNode)i.next();
            w.write(buf.substring(pos, n.beg_charno0));
            n.write(w);
            pos = n.end_charno0;
        }

        w.write("\n\n////// BEGIN OF GENERATED NATIVE METHODS //////\n\n");
        for (int j=0, len = implicitNativeMethods.size(); j<len; j++) {
            implicitNativeMethods.get(j).writeMethod(w);
            w.write("\n");
        }
        w.write("\n////// END OF GENERATED NATIVE METHODS //////\n");

        w.write(buf.substring(pos, this.end_charno0));

        s.setSubst("LOADLIBRARY", oldLib);
        s.setSubst("INDENT", oldInd);
        s.setSubst("CLASSNAME", oldCls);

        w.getNativeWriter().classWriteFinalize();
    }

    public String toString() {
        return (this.type == CLASS ? "class " : "interface ") +
               this.getFullName();
    }

    public String describe() throws ParseException {
        String s = YYModifierList.toString(modifiers) + this.toString();
        if (!isInterface()) s += " extends " + getSuperclass();
        s += "\n";

        s += "implemented interfaces:\n";
        for (String i : getInterfaces().keySet()) s += "    " + i + "\n";

        s += "fields:\n";
        for (String i : getAccessibleFields().keySet()) s += "    " + i + "\n";

        s += "constructors:\n";
        for (String i : getConstructors().keySet()) s += "    " + i + "\n";

        s += "methods:\n";
        for (Map.Entry<String, IMethodInfo> e : getAccessibleMethods().entrySet()) {
            if (classMgr.equals((e.getValue()).getDeclaringClass(), classMgr.Object)) {
                continue;
            }
            s += "    " + e.getKey() + "\n";
        }

        s += "referenced classes:\n";
        for (IClassInfo cls : referencedClasses) {
            s += "    " + cls.getJNIName() + "\n";
        }
        return s;
    }
}