<%@tag description="Empty Page" pageEncoding="UTF-8"%>
<%@attribute name="pagenamebreadcrumb" fragment="true" %>
<%@attribute name="bigtitle" fragment="true" %>
<%@attribute name="smalltitle" fragment="true" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="ex" uri="../tlds/capture.tld" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Capture | Dashboard</title>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
        <!-- bootstrap 3.0.2 -->
        <link href="adminlte/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <!-- font Awesome -->
        <link href="adminlte/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
        <!-- Ionicons -->
        <link href="adminlte/css/ionicons.min.css" rel="stylesheet" type="text/css" />
        <!-- Theme style -->
        <link href="css/fonts.css" rel="stylesheet" type="text/css" />
        <link href="adminlte/css/AdminLTE.css" rel="stylesheet" type="text/css" />

        <link href="css/flow.css" rel="stylesheet" type="text/css" />


        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
        <![endif]-->
    </head>
    <body class="skin-blue">
        <!-- header logo: style can be found in header.less -->
        <header class="header">
            <a href="index.html" class="logo">
                <!-- Add the class icon to your logo image or logo icon to add the margining -->
                Capture Dashboard
            </a>
            <!-- Header Navbar: style can be found in header.less -->
            <nav class="navbar navbar-static-top" role="navigation">
                <!-- Sidebar toggle button-->
                <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <div class="navbar-right">
                    <ul class="nav navbar-nav">
                        <!-- Messages: style can be found in dropdown.less-->
                        <li class="dropdown messages-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-envelope"></i>
                                <span class="label label-success">0</span>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="header">You have 0 messages</li>
                                
                                <li class="footer"><a href="#">See All Messages</a></li>
                            </ul>
                        </li>
                        <!-- Notifications: style can be found in dropdown.less -->
                        <li class="dropdown notifications-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-warning"></i>
                                <span class="label label-warning">0</span>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="header">You have 0 notifications</li>
                                
                                <li class="footer"><a href="#">View all</a></li>
                            </ul>
                        </li>
                        <!-- Tasks: style can be found in dropdown.less -->
                        <li class="dropdown tasks-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-tasks"></i>
                                <span class="label label-danger">0</span>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="header">You have 0 tasks</li>
                                
                                <li class="footer">
                                    <a href="#">View all tasks</a>
                                </li>
                            </ul>
                        </li>
                        <!-- User Account: style can be found in dropdown.less -->
                        <li class="dropdown user user-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="glyphicon glyphicon-user"></i>
                                <span>User<i class="caret"></i></span>
                            </a>
                            <ul class="dropdown-menu">
                                <!-- User image -->
                                <li class="user-header bg-light-blue">
                                    <img src="adminlte/img/avatar-tomas.png" class="img-circle" alt="User Image" />
                                    <p>
                                        User - Capture Manager
                                        <small>Member since Feb. 2015</small>
                                    </p>
                                </li>
                                <!-- Menu Body -->
                                <li class="user-body">
                                    <div class="col-xs-4 text-center">
                                        <a href="#">Followers</a>
                                    </div>
                                    <div class="col-xs-4 text-center">
                                        <a href="#">Sales</a>
                                    </div>
                                    <div class="col-xs-4 text-center">
                                        <a href="#">Friends</a>
                                    </div>
                                </li>
                                <!-- Menu Footer-->
                                <li class="user-footer">
                                    <div class="pull-left">
                                        <a href="#" class="btn btn-default btn-flat">Profile</a>
                                    </div>
                                    <div class="pull-right">
                                        <a href="./logout" class="btn btn-default btn-flat">Sign out</a>
                                    </div>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </header>
        <div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Left side column. contains the logo and sidebar -->
            <aside class="left-side sidebar-offcanvas">                
                <!-- sidebar: style can be found in sidebar.less -->
                <section class="sidebar">
                    <!-- Sidebar user panel -->
                    <div class="user-panel">
                        <div class="pull-left image">
                            <img src="adminlte/img/avatar-tomas.png" class="img-circle" alt="User Image" />
                        </div>
                        <div class="pull-left info">
                            <p>Hello, User!</p>

                            <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
                        </div>
                    </div>
                    <!-- search form -->
                    <form action="#" method="get" class="sidebar-form">
                        <div class="input-group">
                            <input type="text" name="q" class="form-control" placeholder="Search..."/>
                            <span class="input-group-btn">
                                <button type='submit' name='seach' id='search-btn' class="btn btn-flat"><i class="fa fa-search"></i></button>
                            </span>
                        </div>
                    </form>
                    <!-- /.search form -->
                    <!-- sidebar menu: : style can be found in sidebar.less -->
                    <ul class="sidebar-menu">
                    	<li class="">
                    		<a href="./overview#">
                                <i class="fa fa-bar-chart-o"></i></i> <span>Overview</span>
                            </a>
                    	</li>
                    	<li class="treeview active">
                            <a href="#">
                                <i class="fa fa-dashboard"></i></i> <span>Topics</span>
                                <i class="fa fa-angle-left pull-right"></i>
                            </a>
							<ul class="treeview-menu active">
								<c:forEach var="topic" items="${menutopics}">
                                	<li><a href="./stats?topic=${topic}"><i class="fa fa-angle-double-right"></i>${topic}</a></li>
                             	</c:forEach>
                            </ul>
                        </li>
                        
                        <%-- <c:forEach var="topic" items="${topics}">
	                        <li class="active">
	                            <a href="./">
	                                <i class="fa fa-dashboard"></i> <span>${topic}</span>
	                            </a>
	                        </li>
                        
                        </c:forEach> --%>
                        
                        
                        
                        <!-- <li>
                            <a href="new">
                                <i class="fa fa-th"></i> <span>Add New Data Channel</span>
                            </a>
                        </li>
                        
                        <li>
                            <a href="newDataPool">
                                <i class="fa fa-th"></i> <span>Add New Data Pool</span>
                            </a>
                        </li>

                        <li class="treeview">
                            <a href="#">
                                <i class="fa fa-edit"></i> <span>Documentation</span>
                                <i class="fa fa-angle-left pull-right"></i>
                            </a>
                            <ul class="treeview-menu">
                                <li><a href="application.html"><i class="fa fa-angle-double-right"></i> REST Services Docs</a></li>
                             
                            </ul>
                        </li> -->

                    </ul>
                </section>
                <!-- /.sidebar -->
            </aside>

            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side">                
                <!-- Content Header (Page header) -->
                <section class="content-header">
                    <h1>
                        <jsp:invoke fragment="bigtitle"/>
                        <small><jsp:invoke fragment="smalltitle"/></small>
                    </h1>
                    <ol class="breadcrumb">
                        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
                        <li class="active"><jsp:invoke fragment="pagenamebreadcrumb"/></li>
                    </ol>
                </section>

                <!-- Main content -->
                <section class="content">
                
                	<!-- <div class="box-body">
                                    <div class="callout callout-danger">
                                        <h4>Warning!!!</h4>
                                        <p>This GUI stays only for testing reasons! It will be removed in some days or weeks! Please use capture-gui as user interface.</p>
                                        <p>En español: la GUI está aquá solo porque Chema la necesitaba para sus pruebas. En algun momento va a desaparecer (la GUI, no Chema). Es mejor usar el modulo capture-gui. Aquí algunas cosas pueden funcional mal!</p>
                                    </div>

                                </div>
                 -->
                
                	<jsp:doBody/>


                </section><!-- /.content -->
            </aside><!-- /.right-side -->
        </div><!-- ./wrapper -->


        <!-- jQuery 2.0.2 -->
        <script src="bootstrap/jquery.js"></script>
        <!-- Bootstrap -->
        <script src="adminlte/js/bootstrap.min.js" type="text/javascript"></script>
        <!-- AdminLTE App -->
        <script src="adminlte/js/AdminLTE/app.js" type="text/javascript"></script>

		<script src="bootstrap/dynamicform.js"></script>

    </body>
</html>