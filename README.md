# Shrike

Simple, minimalist, lightweight, Inversion of Control and Dependency Injection Framework for Java 11+

# Introduction
	
The API can be used from central repository:

```xml
    <dependencies>
        ....
        <dependency>
            <groupId>me.gilbva</groupId>
            <artifactId>shrike</artifactId>
            <version>1.0</version>
        </dependency>
        ....
    </dependencies>
```

## Components
**Shrike** manages the concept of components, a component is a java annotated class (with the **@Component** annotation), **Shrike** will instantiate this class for you when it is required. A component class may be declared as public, but if you desire to hide the class from other package it may be declared as private too. A **Shrike** component looks like this:
```java
import Component;

@Component
class MyComponent
{
}
```

## Components Injection
The Components can depend on other components, when this is the case **Shrike** will inject the dependencies so you don't have to worry about it. For this purpouse we use the **@Inject** annotation like this:
```java
import Component;
import Inject;

@Component
class OtherComponent
{
    @Inject
    private MyComponent myComp;
}
```

## Shrike.find
The components you declare will never be created until it is needed. So from any program to do something with components at least one component must be obtained through the **Shrike.find()** method like this:
```java
import me.gilbva.shrike.annotations.Ioc;

public class Main
{
    public static void main(String[] args)
    {
        ShrikeIoc.find(MyComponent.class);
    }
}
```

## Initialization
When a component is created sometimes you'll need to do some work with it upon initialization, this can be done with the **ComponentInit** java standard annotation.
```java
import Component;
import Inject;
import ComponentInit;

@Component
class OtherComponent
{
    @Inject
    private MyComponent myComp;

    @ComponentInit
    public void init()
    {
        myComp.doSomething();
    }
}
```

You should use the **ComponentInit** annotation for components initialization because without the constructor the dependencies are not already injected.
```java
import Component;
import Inject;

@Component
class OtherComponent
{
    @Inject
    private MyComponent myComp;

    public OtherComponent()
    {
        // myComp will be null here, as the object is being created.
    }
}
```

All components must have a default constructor otherwise the component cannot be created. The framework will treat any component without a default constructor as if it does not exists.

```java
import Component;
import Inject;

@Component
class OtherComponent
{
    public OtherComponent(String str)
    {
        // This component cannot be created as it does not have a default constructor, a call to
        // ShrikeIoc.find(OtherComponent.class) will result in null
        // And injection will not work either @Inject OtherComponent comp; will result in null as well.
    }
}
```

# Services

A service in **Shrike** is nothing else than a class or generic type used to inject components that extends from or implement it. If a component extends from a class or implements an interface, it is said that the component provides that service.

```java
public interface MyService
{
    void doSomething();
}

@Component
class MyComponent implements MyService
{
    @Override
    public void doSomething()
    {
        System.out.println("doing something");
    }
}
```

In this case the MyComponent component provides the MyService interface, what this means is that you can inject MyService in any other component without the need to know that MyComponent class even exist.
```java
@Component
class OtherComponent
{
    @Inject
    private MyService serv;

    @PostConstruct
    public void init()
    {
        serv.doSomething();
    }
}
```

The purpose of **Shrike** is to provide this kind of behavior of loose coupling. So you can write components depending on the specifications and not the implementation.

## Generic Services
The services can be generic types and **Shrike** will take notice of this, so you can have something like the following:
```java
public interface MyService<T>
{
    void doSomething(T data);
}

@Component
class MyComponent implements MyService<String>
{
    @Override
    public void doSomething(String data)
    {
        System.out.println("doing something with " + data);
    }
}

@Component
class OtherComponent
{
    @Inject
    private MyService<String> serv;

    @PostConstruct
    public void init()
    {
        serv.doSomething("Shrike Framework");
    }
}
```

The generic type may be as complex as you like. Here are some examples of what type of service you may declare.

MyService with String array
```java
class PrintAllStringComponent implements MyService<String[]>
....
@Inject
private MyService<String[]> serv;

```

MyService with String list
```java
class PrintStringListComponent implements MyService<List<String>>
....
@Inject
private MyService<List<String>> serv;

```

MyService with some interface called Repository of User class
```java
class UserRepositoryServicesComponent implements MyService<Repository<User>>
....
@Inject
private MyService<Repository<User>> serv;

```

## Injecting multiple services implementations
Multiple components may implement the same service, you can then inject all of then like this:
```java
public interface MyService

@Component
class MyComponent1 implements MyService

@Component
class MyComponent2 implements MyService

@Inject
private MyService[] serv;
// or with list
@Inject
private List<MyService> serv;
// or with set
@Inject
private Set<MyService> serv;

```

## Component priority
In this case the framework will inject MyComponent1 and MyComponent2 on the serv field; you can then use it as you like: iterate, add, remove, etc. The order will not be defined in this example, but if the components needs to be in a specific order that must be specified in it's priorities, like this:
```java
@Component
@Priority(1)
class MyComponent1 implements MyService

@Component
@Priority(2)
class MyComponent2 implements MyService

```

A lower priority number means a higher priority component, in this example MyComponent1 will be injected before the MyComponent2, if priority annotation is not present then the default priority is **Integer.MAX_VALUE**, which means that components with no priority will be the last to be injected.
If you have several components implementing the same service and you like to inject only one, then only the higher priority component will be injected, if several components have the same priority the injected component is not determined, this means that if you must specify the priority of the component to a lower number than the other components providing the same service so this component will be the default one for that service.

## Contexts

All the work of the frameworks happends in a context, a context is the container in wich components are created and mapped to each other. the default context is the **APPLICATION** context it´s represented by the org.bridje.ioc.Application class and it can be obtained with the **ShrikeIoc.context()** method.
```java
IocContext<Application> appContext = ShrikeIoc.context();
```

The context may be injected also on any component like this:
```java
@Component
class MyComponent
{
    @Inject
    private IocContext<Application> appContext;
}

```

## Scopes 
And scope is a class that determines which components are handled in a context. The scopes need to implement the **me.gilbva.shrike.annotations.Scope** interface like this:

```java
public class MyScopeObject implements Scope
{
    @Override
    public void preCreateComponent(Class<Object> clazz)
    {
         //Called by the context before a component instantiation.
    }

    @Override
    public void preInitComponent(Class<Object> clazz, Object instance)
    {
         //Called by the context after the dependencies has being injected.
    }

    @Override
    public void postInitComponent(Class<Object> clazz, Object instance)
    {
         //Called by the context after the PostConstruct methods has being called.
    }
}
```

## Child Contexts
All context have a scope, the default context is the Application scoped context, only one Application context may exists as there is no way to create another. But you may create as many scopes as you want, and as many context as you want for these scopes. 

The @Component annotation can specify on with scope this component must be created.
```java
@Component(scope = MyScopeObject.class)
class SomeComponent
```

This component will not be available from the IocContext<Application> context, which means that a child context must be created with the **MyScopeObject** scope, as this.

```java
IocContext<MyScopeObject> childContext = Ioc.context().createChildContext(new MyScopeObject());
// or
@Inject
private IocContext<Application> appContext;
....
IocContext<MyScopeObject> childContext = appContext.createChildContext(new MyScopeObject());
```

Then you can obtain the component from the context in which it lives.

```java
....
SomeComponent someComp = childContext.find(SomeComponent.class);
```

## @InjectNext annotation
The InjectNext annotation was created to allow the chain of responsability pattern into the components.

**When to use the Chain of Responsability Pattern**:

   - More than one objects may handle a request, and the handler isn’t known a priori. The handler should be ascertained automatically.
   -You want to issue a request to one of several objects without specifying the receiver explicitly.
   - The set of objects that can handle a request should be specified dynamically.

In this pattern exist three important elements:

1. **Handler**:
  -Defines an interface for handling requests.
  - (Optionally) Implements the successor link.
  
2. **ConcreteHandler**:
  -Handles requests it is responsible for.
  -Can access its successor.
  -If the ConcreteHandler can handle the request, it does so; otherwise it forwards the request to its successor.

3. **Client**:
  - Initiates the request to a ConcreteHandler object on the chain.  

When a client issues a request, the request propagates along the chain until a ConcreteHandler object takes responsibility for
handling it.

Then to use the InjectNext annotation you must create some common interface to all the components in the chain, like this.

```java
public interface MyChainHandler<T>    //This interface is the Handler.
{
    T execute(T prev);
}
```

Then you can create as many components as you whant for the chain. 
Every implementation of components represent the **ConcreteHandler** element.
```java
@Component
@Priority(1)
public class ChainHandlerFirst implements MyChainHandler<String>
{
    @InjectNext
    private MyChainHandler<String> next;
    
    @Override
    public String execute(String prev)
    {
        if(someCOndition)
        {
             return next.execute(c);
        }
        return "somedata";
    }
    
}
.....
@Component
@Priority(2)
public class ChainHandlerSecond implements MyChainHandler<String>
{
    @InjectNext
    private MyChainHandler<String> next;
    
    @Override
    public String execute(String prev)
    {
        .....
    }
    
}

@Component
@Priority(3)
public class ChainHandlerThird implements MyChainHandler<String>
{
    @InjectNext
    private MyChainHandler<String> next;

    @Override
    public String execute(String prev)
    {
        .....
    }
}
```

Notice that all the components in the chain inject the **MyChainHandler<String> next;** component, with the **@InjectNext** annotation, the framework will inject the next available component in the chain, this is a component that provides the same service and that has a lesser priority (a higher number) than the current component. By using this pattern you can inject components one inside the others creating a chain.

```
ChainHandlerFirst -> ChainHandlerSecond -> ChainHandlerThird
```

Then you can use this chain by injectig the highest priority component in the chain into any component you whant.

```java
@Component
public class SomeComponent
{
    @Inject
    private MyChainHandler<String> first;
    
    public String execute()
    {
        return first.execute(null);
    }
}
```
