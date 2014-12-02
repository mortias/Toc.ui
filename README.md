
# Toc.ui

![GitHub Overview](https://github.com/mortias/Toc.ui/raw/master/toc-docs/toc.ui2.png)

## Introduction 

The TOC tool is mainly a link manager. It displays your links in a sort of grouped project overview, but with executable actions behind it, which is very handy to use as a shortcut tool. However it is the architecture behind it which makes it most interesting!

When you'll start working within a microservice based architecture you'll quickly end up with a lot of different servers, tools, links, etc. For this i've crated a tool that not only acts as a shortcut tool, but also is able to execute programs, and verify the things you link to. The Toc.ui is fully portable.

You could also use this as a DevOps tool to monitor your services. Also this is easy configurable because it's using basic HTML and jQuery as a layout manager. It also comes out of the box with some handy tools like Swagger UI, Hawt.io for Jmx and Link verification.

## Architecture / overview
![GitHub Architecture](https://github.com/mortias/Toc.ui/raw/master/toc-docs/toc.ui.png)

## Main features 

- Simple Html based GUI (using jQuery for layouts, skinnable)
- Can launch ANY command line statement (batch files, programs, documentations, etc.)
- Fully encrypted (PBEWithMD5AndDES), runnable statements with passwords, binaries and executables
- Easy configurable (using a single .yaml file)
- Native REST support (Swagger api running on Jetty)
- Asynchronous embedded task executions supported by Vert.x / SockJs (EventBus)
- Is able to verify the status/availability of your links
- Async example service which displays your cpu load with d3js over SockJs
- JavaFx based, so you can extend the tool at your own will
- Handy Swagger UI (visualize and consume your RESTful services)
- Hawt.io (deployable on embedded Jetty using jolokia, ideal for showing Camel routes)
- Link verification (with or without proxy settings)

## Possible new features
- Adding a project based chat
- Adding shared "Wiki style" project pages
- A simple shareable kanban board
- Adding an embedded mailserver or scheduler

## The sky is the limit!
- Appart from creating shortcuts and verifying links you can make the most out of the embedded Vert.x/SockJs and Rest servers, for project monitoring, notifications, automation, shared content etc. Almost anything is possible because you have access to the underlying OS you are running, and you have an eventbus and restservice at your disposal.

## Disclaimer
This tool provides the possibility to run and execute binaries on your OS, use this with care as i'm not responsible for direct, indirect, incidental or consequential damages resulting from any defect, error or failure of this software.

## How to Run it
It is a standard maven build so you can create a new project from the main pom you can find in the toc-modules
mvn clean package and you are good to go to run the com.mitc.Toc class

## How to Use it

### Simple link 
```
<a href="http://link.springer.com" title="">link</a>
```
### Link to a binary file in your site folder
```
<a href="${bin}\books\camel_in_action.pdf" title="">link</a>
```
### Link to a bat file which launches Toad with a specific schema
```
<a href="${bin}\database\dev\db.bat" title="">link</a>

bat file:
start "" "C:/Program Files (x86)/Quest Software/Toad for Oracle 11.6/Toad.exe" -c USER/PASSWORD
exit
```
### Link to open a ftp site 
```
<a href="${bin}\ftp\ftp-server.bat" title="">FTP</a>

bat file:
start "" "C:/Program Files (x86)/FileZilla FTP Client/filezilla" --site="0/01 my ftp site"
exit
```   
### Link to open a remote server connection
```
<a href="${bin}\remote\rdp-server.rdp" title="">RDP</a>
```

## License
Toc.ui is distributed under Apache 2.0 License.
