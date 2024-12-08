/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
 * 
 * Contributors:
 *     (teslanet.nl) Rogier Cobben - initial creation
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package nl.teslanet.mule.connectors.coap.test.utils;


import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.config.api.SpringXmlConfigurationBuilderFactory.createConfigurationBuilder;
import static org.mule.runtime.core.api.config.bootstrap.ArtifactType.APP;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.stopIfNeeded;
import static org.mule.runtime.core.api.util.FileUtils.deleteTree;
import static org.mule.runtime.core.api.util.FileUtils.newFile;
import static org.mule.tck.junit4.TestsLogConfigurationHelper.clearLoggingConfig;
import static org.mule.tck.junit4.TestsLogConfigurationHelper.configureLoggingForTest;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mule.functional.junit4.ArtifactFunctionalTestCase;
import org.mule.functional.junit4.TestServicesMuleContextConfigurator;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.scheduler.SchedulerView;
import org.mule.runtime.api.serialization.ObjectSerializer;
import org.mule.runtime.api.util.concurrent.Latch;
import org.mule.runtime.config.internal.SpringXmlConfigurationBuilder;
import org.mule.runtime.container.internal.ContainerClassLoaderFactory;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.config.ConfigurationBuilder;
import org.mule.runtime.core.api.config.DefaultMuleConfiguration;
import org.mule.runtime.core.api.config.MuleConfiguration;
import org.mule.runtime.core.api.config.bootstrap.ArtifactType;
import org.mule.runtime.core.api.config.builders.SimpleConfigurationBuilder;
import org.mule.runtime.core.api.context.DefaultMuleContextFactory;
import org.mule.runtime.core.api.context.MuleContextBuilder;
import org.mule.runtime.core.api.context.MuleContextFactory;
import org.mule.runtime.core.api.context.notification.MuleContextNotification;
import org.mule.runtime.core.api.context.notification.MuleContextNotificationListener;
import org.mule.runtime.core.internal.context.DefaultMuleContextBuilder;
import org.mule.runtime.core.internal.serialization.JavaObjectSerializer;
import org.mule.runtime.module.artifact.api.classloader.ArtifactClassLoader;
import org.mule.runtime.module.artifact.api.classloader.ClassLoaderRepository;
import org.mule.runtime.module.artifact.api.classloader.net.MuleArtifactUrlStreamHandler;
import org.mule.runtime.module.artifact.api.classloader.net.MuleUrlStreamHandlerFactory;
import org.mule.runtime.module.service.api.discoverer.ServiceDiscoverer;
import org.mule.runtime.module.service.api.manager.ServiceManager;
import org.mule.tck.SimpleUnitTestSupportSchedulerService;
import org.mule.tck.config.TestServicesConfigurationBuilder;
import org.mule.test.runner.ApplicationClassLoaderAware;
import org.mule.test.runner.ContainerClassLoaderAware;
import org.mule.test.runner.PluginClassLoadersAware;
import org.mule.test.runner.ServiceClassLoadersAware;
import org.mule.test.runner.api.IsolatedClassLoaderExtensionsManagerConfigurationBuilder;
import org.mule.test.runner.api.IsolatedServiceProviderDiscoverer;
import org.slf4j.Logger;


@SuppressWarnings( "deprecation" )
public abstract class AbstractMuleStartTestCase
{
    private static final Logger LOGGER= getLogger( AbstractMuleStartTestCase.class.getCanonicalName() );

    /**
     * As part of providing support for handling different artifacts without unzipping them, the factory for URL must be registered
     * and then the current protocol for mule artifacts {@link MuleArtifactUrlStreamHandler#PROTOCOL}.
     */
    static
    {
        // register the custom UrlStreamHandlerFactory.
        MuleUrlStreamHandlerFactory.installUrlStreamHandlerFactory();
        MuleArtifactUrlStreamHandler.register();
    }

    public static final String SPRING_CONFIG_FILES_PROPERTIES= "spring.config.files";

    public static final String MULE_SPRING_CONFIG_FILE= "mule-spring-config.xml";

    private static List< ArtifactClassLoader > pluginClassLoaders;

    private static List< ArtifactClassLoader > serviceClassLoaders;

    private static ClassLoader containerClassLoader;

    private static ClassLoader applicationClassLoader;

    private static ServiceManager serviceRepository;

    @SuppressWarnings( "unused" )
    private static ClassLoaderRepository classLoaderRepository;

    private static IsolatedClassLoaderExtensionsManagerConfigurationBuilder extensionsManagerConfigurationBuilder;

    private static TestServicesMuleContextConfigurator serviceConfigurator;

    /**
     * The executionClassLoader used to run this test. It will be created per class or per method depending on
     * {@link #disposeContextPerClass}.
     */
    private static ArtifactClassLoader executionClassLoader;

    public TestServicesConfigurationBuilder testServicesConfigurationBuilder;

    public Supplier< TestServicesConfigurationBuilder > testServicesConfigurationBuilderSupplier= () -> new TestServicesConfigurationBuilder( false, false );

    /**
     * Top-level directories under <code>.mule</code> which are not deleted on each test case recycle. This is required, e.g. to
     * play nice with transaction manager recovery service object store.
     */
    public static final String[] IGNORED_DOT_MULE_DIRS= new String []{ "transaction-log" };

    public static final String WORKING_DIRECTORY_SYSTEM_PROPERTY_KEY= "workingDirectory";

    TemporaryFolder workingDirectory= new TemporaryFolder();

    @Rule
    public TestName name= new TestName();

    /**
     * Default shutdown timeout used when {@link #isGracefulShutdown()} is {@code false}.
     */
    protected static final long NON_GRACEFUL_SHUTDOWN_TIMEOUT= 10;

    /**
     * The Mule context
     */
    protected static MuleContext muleContext= null;

    /**
     * Override to use the name of application tested
     * @return the name of the application
     */
    protected String getApplicationName()
    {
        return "";
    }

    /**
     * Overide this method to set the mule config to test
     * @return coma separated paths to mule configs
     */
    protected String getConfigResources()
    {
        return "mule-config.xml";
    }

    /**
     * Override to set expected exception
     */
    protected void expectException()
    {
        //NONE
    }

    /**
     * Exception rule
     */
    @Rule
    public ExpectedException exception= ExpectedException.none();

    /**
     * no setup actions needed
     */
    @Before
    public final void setUp()
    {
        System.getProperties().put( "mule.testingMode", "true" );
        //mvn clean test -Dmule.testingMode=true 
    }

    /**
     * mule cleanup
     */
    @After
    public final void tearDown()
    {
        if ( muleContext != null && !muleContext.isDisposed() )
        {
            try
            {
                muleContext.stop();
            }
            catch ( MuleException e )
            {
                e.printStackTrace();
            }
            muleContext.dispose();
        }
    }

    /**
     * Test whether the mule configuration can be started
     * @throws MuleException thrown when mule could not start with given configuration
     */
    @Test
    @Ignore("not working")
    public void startMuleTest() throws MuleException
    {
        expectException();

        SpringXmlConfigurationBuilder configBuilder;
        configBuilder= new SpringXmlConfigurationBuilder( getConfigResources() );
        DefaultMuleConfiguration muleConfig= new DefaultMuleConfiguration();
        muleConfig.setId( "testapplication" );
        MuleContextBuilder contextBuilder= new DefaultMuleContextBuilder( ArtifactType.APP );
        contextBuilder.setMuleConfiguration( muleConfig );
        MuleContextFactory contextFactory= new DefaultMuleContextFactory();
        MuleContext muleContext;

        muleContext= contextFactory.createMuleContext( configBuilder, contextBuilder );
        muleContext.start();
    }

    /**
     * Test whether the mule configuration can be started
     * @throws MuleException thrown when mule could not start with given configuration
     */
    @Test
    @Ignore("not working in mule 4")
    public void startMuleTest0() throws MuleException
    {
        expectException();
        //        GenericXmlApplicationContext x;
        //        ArtifactDescriptor y;
        //        ArtifactClassLoader classLoader= new ArtifactClassLoader( );
        //        x= CoAPConnector.class;
        DefaultMuleContextBuilder ctxBuilder= null;

        //IsolatedClassLoaderExtensionsManagerConfigurationBuilder isolatedBuilder= new IsolatedClassLoaderExtensionsManagerConfigurationBuilder(null);
        DefaultMuleContextFactory muleContextFactory= new DefaultMuleContextFactory();
        //IsolatedClassLoaderExtensionsManagerConfigurationBuilder z;
        SpringXmlConfigurationBuilder configBuilder= new SpringXmlConfigurationBuilder( getConfigResources() );
        muleContext= muleContextFactory.createMuleContext( configBuilder, ctxBuilder );
        muleContext.start();
        muleContext.stop();
        muleContext.dispose();
    }

    /**
     * Test whether the mule configuration can be started
     * @throws MuleException thrown when mule could not start with given configuration
     */
    @Test
    @Ignore("not working in mule 4")
    public void startMuleTest1() throws MuleException
    {
        expectException();
        DefaultMuleContextFactory muleContextFactory= new DefaultMuleContextFactory();
        SpringXmlConfigurationBuilder configBuilder= new SpringXmlConfigurationBuilder( getConfigResources() );
        muleContext= muleContextFactory.createMuleContext( configBuilder );
        muleContext.start();
        muleContext.stop();
        muleContext.dispose();
    }

    @Test
    @Ignore("not working in mule 4")
    public void startMuleTest2() throws Exception
    {

        boolean logConfigured= false;
        if ( !logConfigured )
        {
            configureLoggingForTest( getClass() );
            logConfigured= true;
        }
        workingDirectory.create();
        String workingDirectoryOldValue= System.setProperty( WORKING_DIRECTORY_SYSTEM_PROPERTY_KEY, workingDirectory.getRoot().getAbsolutePath() );
        try
        {
            muleContext= createMuleContext();
            muleContext.getInjector().inject( this );
            classLoaderRepository= new TestClassLoaderRepository();
            if ( muleContext != null && !muleContext.isStarted() )
            {
                startMuleContext();
            }
        }
        finally
        {
            if ( workingDirectoryOldValue != null )
            {
                System.setProperty( WORKING_DIRECTORY_SYSTEM_PROPERTY_KEY, workingDirectoryOldValue );
            }
            else
            {
                System.clearProperty( WORKING_DIRECTORY_SYSTEM_PROPERTY_KEY );
            }
        }
    }

    protected MuleContext createMuleContext() throws Exception
    {
        // Should we set up the manager for every method?
        MuleContext context;
        if ( muleContext != null )
        {
            context= muleContext;
        }
        else
        {
            final ClassLoader executionClassLoader= getExecutionClassLoader();
            final ClassLoader originalContextClassLoader= currentThread().getContextClassLoader();
            try
            {
                currentThread().setContextClassLoader( executionClassLoader );

                MuleContextFactory muleContextFactory= new DefaultMuleContextFactory();
                List< ConfigurationBuilder > builders= new ArrayList<>();
                builders.add( new SimpleConfigurationBuilder( getStartUpRegistryObjects() ) );
                addBuilders( builders );
                builders.add( getBuilder() );
                MuleContextBuilder contextBuilder= MuleContextBuilder.builder( APP );
                DefaultMuleConfiguration muleConfiguration= new DefaultMuleConfiguration();
                String workingDirectory= this.workingDirectory.getRoot().getAbsolutePath();
                LOGGER.info( "Using working directory for test: " + workingDirectory );
                muleConfiguration.setWorkingDirectory( workingDirectory );
                muleConfiguration.setId( this.getClass().getSimpleName() + "#" + name.getMethodName() );
                contextBuilder.setMuleConfiguration( muleConfiguration );
                contextBuilder.setExecutionClassLoader( executionClassLoader );
                contextBuilder.setObjectSerializer( getObjectSerializer() );
                contextBuilder.setDeploymentProperties( getDeploymentProperties() );
                //                configureMuleContext( contextBuilder );
                context= muleContextFactory.createMuleContext( builders, contextBuilder );
                recordSchedulersOnInit( context );
                if ( !isGracefulShutdown() )
                {
                    // Even though graceful shutdown is disabled allow small amount of time to avoid rejection errors when stream emits
                    // complete signal
                    ( (DefaultMuleConfiguration) context.getConfiguration() ).setShutdownTimeout( NON_GRACEFUL_SHUTDOWN_TIMEOUT );
                }
            }
            finally
            {
                currentThread().setContextClassLoader( originalContextClassLoader );
            }
        }
        return context;
    }

    private void startMuleContext() throws MuleException, InterruptedException
    {
        final AtomicReference< Latch > contextStartedLatch= new AtomicReference<>();

        contextStartedLatch.set( new Latch() );
        // Do not inline it, otherwise the type of the listener is lost
        final MuleContextNotificationListener< MuleContextNotification > listener= new MuleContextNotificationListener< MuleContextNotification >()
            {

                @Override
                public boolean isBlocking()
                {
                    return false;
                }

                @Override
                public void onNotification( MuleContextNotification notification )
                {
                    contextStartedLatch.get().countDown();
                }
            };
        muleContext.getNotificationManager().addListener( listener );

        muleContext.start();

        contextStartedLatch.get().await( 20, SECONDS );
    }

    protected ClassLoader getExecutionClassLoader()
    {
        if ( executionClassLoader == null )
        {
            executionClassLoader= new ContainerClassLoaderFactory().createContainerClassLoader( getClass().getClassLoader() );
        }

        return executionClassLoader.getClassLoader();
    }

    protected Map< String, Object > getStartUpRegistryObjects()
    {
        return emptyMap();
    }

    // This shouldn't be needed by Test cases but can be used by base testcases that wish to add further builders when
    // creating the MuleContext.
    protected void addBuilders( List< ConfigurationBuilder > builders )
    {
        testServicesConfigurationBuilder= testServicesConfigurationBuilderSupplier.get();
        builders.add( testServicesConfigurationBuilder );
    }

    /**
     * @return the {@link ObjectSerializer} to use on the test's {@link MuleContext}
     */
    protected ObjectSerializer getObjectSerializer()
    {
        return new JavaObjectSerializer();
    }

    /**
     * @return the deployment properties to be used
     */
    protected Optional< Properties > getDeploymentProperties()
    {
        return Optional.empty();
    }

    private static List< SchedulerView > schedulersOnInit;

    protected static void recordSchedulersOnInit( MuleContext context )
    {
        if ( context != null )
        {
            final SchedulerService serviceImpl= context.getSchedulerService();
            schedulersOnInit= serviceImpl.getSchedulers();
        }
        else
        {
            schedulersOnInit= emptyList();
        }
    }

    /**
     * Determines if the test case should perform graceful shutdown or not. Default is false so that tests run more quickly.
     */
    protected boolean isGracefulShutdown()
    {
        return false;
    }

    protected ConfigurationBuilder getBuilder() throws Exception
    {
        String configResources= getConfigResources();
        ConfigurationBuilder builder= createConfigurationBuilder( configResources, emptyMap(), APP, false, false );
        builder.addServiceConfigurator( serviceConfigurator );
        return builder;
    }

    public final void disposeContextPerTest() throws Exception
    {

        if ( muleContext != null && muleContext.isStarted() )
        {
            muleContext.stop();
        }

        disposeContext();
        if ( testServicesConfigurationBuilder != null )
        {
            testServicesConfigurationBuilder.stopServices();
        }
        //            doTearDownAfterMuleContextDispose();

        // When an Assumption fails then junit doesn't call @Before methods so we need to avoid
        // executing delete if there's no root folder.
        workingDirectory.delete();
    }

    public static void disposeContext() throws MuleException
    {
        try
        {
            if ( muleContext != null && !( muleContext.isDisposed() || muleContext.isDisposing() ) )
            {
                try
                {
                    muleContext.dispose();
                }
                catch ( IllegalStateException e )
                {
                    // Ignore
                    LOGGER.warn( e + " : " + e.getMessage() );
                }

                verifyAndStopSchedulers();

                MuleConfiguration configuration= muleContext.getConfiguration();

                if ( configuration != null )
                {
                    final String workingDir= configuration.getWorkingDirectory();
                    // do not delete TM recovery object store, everything else is good to
                    // go
                    deleteTree( newFile( workingDir ), IGNORED_DOT_MULE_DIRS );
                }
            }
            deleteTree( newFile( "./ActiveMQ" ) );
        }
        finally
        {
            muleContext= null;
            clearLoggingConfig();
        }
    }

    protected static void verifyAndStopSchedulers() throws MuleException
    {
        final SchedulerService serviceImpl= muleContext.getSchedulerService();

        Set< String > schedulersOnInitNames= schedulersOnInit.stream().map( s -> s.getName() ).collect( toSet() );
        try
        {
            assertThat(
                muleContext.getSchedulerService().getSchedulers().stream().filter( s -> !schedulersOnInitNames.contains( s.getName() ) ).collect( toList() ),
                org.hamcrest.collection.IsEmptyCollection.empty()
            );
        }
        finally
        {
            if ( serviceImpl instanceof SimpleUnitTestSupportSchedulerService )
            {
                stopIfNeeded( serviceImpl );
            }
        }

    }

    private static void createServiceManager()
    {
        serviceRepository= ServiceManager.create( ServiceDiscoverer.create( new IsolatedServiceProviderDiscoverer( serviceClassLoaders ) ) );
        try
        {
            serviceRepository.start();
        }
        catch ( MuleException e )
        {
            throw new IllegalStateException( "Couldn't start service manager", e );
        }
        serviceConfigurator= new TestServicesMuleContextConfigurator( serviceRepository );
    }

    @PluginClassLoadersAware
    private static final void setPluginClassLoaders( List< ArtifactClassLoader > artifactClassLoaders )
    {
        if ( artifactClassLoaders == null )
        {
            throw new IllegalArgumentException( "A null value cannot be set as the plugins class loaders" );
        }

        if ( pluginClassLoaders != null )
        {
            throw new IllegalStateException( "Plugin class loaders were already set, it cannot be set again" );
        }
        pluginClassLoaders= artifactClassLoaders;
        if ( !pluginClassLoaders.isEmpty() )
        {
            extensionsManagerConfigurationBuilder= new IsolatedClassLoaderExtensionsManagerConfigurationBuilder( pluginClassLoaders );
            extensionsManagerConfigurationBuilder.loadExtensionModels();
        }
    }

    @ServiceClassLoadersAware
    private static final void setServiceClassLoaders( List< ArtifactClassLoader > artifactClassLoaders )
    {
        if ( artifactClassLoaders == null )
        {
            throw new IllegalArgumentException( "A null value cannot be set as the services class loaders" );
        }

        if ( serviceClassLoaders != null )
        {
            throw new IllegalStateException( "Service class loaders were already set, it cannot be set again" );
        }
        serviceClassLoaders= artifactClassLoaders;
        createServiceManager();
    }

    @ContainerClassLoaderAware
    private static final void setContainerClassLoader( ClassLoader newcontainerClassLoader )
    {
        if ( newcontainerClassLoader == null )
        {
            throw new IllegalArgumentException( "A null value cannot be set as the container classLoader" );
        }

        if ( containerClassLoader != null )
        {
            throw new IllegalStateException( "Container classloader was already set, it cannot be set again" );
        }

        containerClassLoader= newcontainerClassLoader;
    }

    @ApplicationClassLoaderAware
    private static final void setApplicationClassLoader( ClassLoader newapplicationClassLoader )
    {
        if ( newapplicationClassLoader == null )
        {
            throw new IllegalArgumentException( "A null value cannot be set as the application classLoader" );
        }

        if ( applicationClassLoader != null )
        {
            throw new IllegalStateException( "Application classloader was already set, it cannot be set again" );
        }

        applicationClassLoader= newapplicationClassLoader;
    }

    /**
     * Defines a {@link ClassLoaderRepository} with all the class loaders configured in the {@link ArtifactFunctionalTestCase}
     * class.
     */
    protected static class TestClassLoaderRepository implements ClassLoaderRepository
    {

        private Map< String, ClassLoader > classLoaders= new HashMap<>();

        public TestClassLoaderRepository()
        {
            registerClassLoader( Thread.currentThread().getContextClassLoader() );
            for ( Object classLoader : serviceClassLoaders )
            {
                registerClassLoader( classLoader );
            }
            for ( Object classLoader : pluginClassLoaders )
            {
                registerClassLoader( classLoader );
            }
        }

        private void registerClassLoader( Object classLoader )
        {
            if ( isArtifactClassLoader( classLoader ) )
            {
                try
                {
                    Method getArtifactIdMethod= classLoader.getClass().getMethod( "getArtifactId" );
                    String artifactId= (String) getArtifactIdMethod.invoke( classLoader );
                    classLoaders.put( artifactId, (ClassLoader) classLoader );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }

        }

        private boolean isArtifactClassLoader( Object classLoader )
        {
            Class< ? > clazz= classLoader.getClass();
            while ( clazz.getSuperclass() != null )
            {
                for ( Class< ? > interfaceClass : clazz.getInterfaces() )
                {
                    if ( interfaceClass.getName().equals( ArtifactClassLoader.class.getName() ) )
                    {
                        return true;
                    }
                }
                clazz= clazz.getSuperclass();
            }

            return false;
        }

        @Override
        public Optional< ClassLoader > find( String classLoaderId )
        {
            return ofNullable( classLoaders.get( classLoaderId ) );
        }

        @Override
        public Optional< String > getId( ClassLoader classLoader )
        {
            for ( String key : classLoaders.keySet() )
            {
                if ( classLoaders.get( key ) == classLoader )
                {
                    return of( key );
                }
            }

            return empty();
        }
    }
}
