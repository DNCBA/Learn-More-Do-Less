1.反射:
	1.什么是反射?可以做什么?
		反射:在运行时,我们可以获取任意一个类的所有方法和属性
			在运行时,我们可以调用一个对象的所有方法和属性
		反射的前提:要获取类的对象(class对象)(获取class对象的三种方法,1.通过object类的getclass方法2.通过类名直接获取.class属性3.通过Class.forNmae(包含包名的类名)来获取)
	2.通过反射操作构造方法
		获取构造方法 getConstructors():返回所有的构造方法
				  getConstructor(参数列表):返回指定参数列表的构造方法
			只能获取public 的构造方法
			通过构造方法类的对象使用newInstance()可以创建一个对象
		创建对象的时候使用构造方法
	3.通过反射操作成员变量
		获取成员变量的方法:
						getFields()
						getField()
						getDeclaredField()
		给成员变量赋值,获取成员变量的值
					Field类的方法:
						Get(对象)
						Set(对象,值)
						setAccessible():设置或者取消Java的访问检查,暴力访问
	4.通过反射操作成员方法
		获取成员方法的方法:
						getMethod("方法名",参数列表)
				获取无参无返回值的成员方法:
						c.getMethod("成员方法名")
				获取无参有返回值的方法;
						c.getMethod("成员方法")
				获取有参有返回值的方法:
						c.getMethod(成员方法",String.class,int.class..)
		调用
			使用method类中的invoke方法
			m.invoke(对象名,参数列表)
	5.步骤:
		1.获取字节码对象:Class p=Class.forName(String path)
		2.通过字节码对象获取构造方法:Constructor c=p.getConstructor(参数列表)
		3.通过构造器构造成员对象:Object person=c.newInstance(参数列表)
		4.通过字节码对象获取类的成员变量:Field f=p.getField(String 参数名)
		5.通过field对象来操作成员变量,get()获得set()修改
		6.通过字节码对象获取成员方法:Method m=p.getMethod(参数列表)
		7.通过Method对象操作成员方法:m.invoke(对象,参数列表)
2.JavaBean
	JavaBean标准类;用于封装数据
	要求:
		类被public修饰
		成员变量被private修饰并且提供对应的getter和setter方法
		有一个公共的无参构造,有参构造不强求
		实现序列号接口
3.BeanUtils工具包
	方法:
		setProperty(对象名,成员变量名,成员变量值)
		getProperty(对象名,成员变量名)
		Property(对象名,Map集合<变量名,变量值>)
