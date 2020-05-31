<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta content="text/html; charset=ISO-8859-1" http-equiv="content-type">
  <title>mmj2 Installation</title>
</head>

<body style="color: rgb(0, 0, 0); background-color: rgb(255, 255, 255);"
 vlink="#ff0000" alink="#000088" link="#0000ff">

<p>
<h1>Brief install instructions</h1>
<p>
In brief:
<ul>
<li>Install Java.
    Java version 11 and 8 are known to work, 11+ is recommended and easier
    to use (we provide a precompiled version).
    You only <i>need</i> the Java Runtime Environment (JRE), but
    unless you're short of disk space, you should probably install the
    full Java Development Kit (JDK).
    Beware: the license for Oracle's Java implementation changed in 2019.
    Make sure you use an OpenJDK implementation of Java,
    fully comply with Oracle's terms for free use of Oracle JDK, or
    pay Oracle their Oracle JDK fee and comply with those different legal terms.
<li>(Recommended) Install git and a good text editor.
<li>Put the mmj2 directory in its conventional place, which is
    C:\mmj2 for Windows and $HOME/mmj2 for anything else.
<li>(Optional) Compile mmj2. This isn't necessary if you use Java 11,
    because we provide a precompiled jar file.
<li>(Recommended) Install the Metamath database set.mm in its
     conventional place, which is C:\set.mm for Windows
     and $HOME/set.mm for anything else.
<li>(Recommended) Install Metamath-exe program in its
     conventional place, which is C:\metamath for Windows
     and $HOME/metamath for anything else.
</ul>
<p>
Then follow the <a href="QuickStart.html">QuickStart.html</a>
<p>
Below is more detail on how to do this for various systems.

<p>
<h1>Full install instructions</h1>
<p>
First, <i>back up your data</i>.
We don't know of any problems, but everyone is human, there
are no guarantees with anything, and we expressly state there's no warranty.
In fact, you should have backups whether or not you ever use this software.

<p>
mmj2 requires Java to run.
Some major versions of Java are "long term support" (LTS) versions.
In particular, major versions 8 and 11 are long term support versions.
We recommend using version 11.
There are also various implementations of Java, in particular,
Oracle's Java implemenation and various OpenJDK implementations.

<p>
<b><i>BEWARE</i></b>: Oracle's implementation of Java
has historically been widely used, but
Oracle's Java license <i>radically</i> changed on April 16, 2019.
Oracle permits certain non-commercial uses at no cost,
but <i>many</i> uses require paying a fee - even some you might think
are non-commercial.
If you don't pay those fees to Oracle ahead-of-time,
you risk large fines for you and any company you work for,
and Oracle has a reputation for being litigious.
If you use the Oracle implementation, make <i>sure</i> all
your uses meet Oracle's license terms.
If you're not sure you'll meet Oracle's terms, consult your lawyer.
An alternative is to use one of the various "openjdk" implementations
of Java, which work fine and is what we suggest below.

<p>
The text below shows how to install mmj2 for various systems:
Windows, Linux/Unix/Cygwin, and MacOS (MacOS is really a kind of Unix).
Java includes the "Swing" class which is used to implement a portable GUI.

<p>
<h2>Windows</h2>
<p>
These are the instructions for a conventional Windows install.
You can also install Cygwin on Windows and use mmj2; if you do that,
follow the Windows instructions for installing Java, then switch to
the Linux/Unix/Cygwin instructions.

<p>
<h3>Install Java</h3>
<p>
First, install Java.
Tehcnically you only need a Java Runtime Environment (JRE)
implementation, but unless you're short of space it doesn't hurt to have a
full Java Development Kit (JDK) since the JDK includes a JRE.

<p>
Legally the safest thing to do is install an implementation
of OpenJDK.
We recommend JDK version 11 (at least) to run.
There are many ways to get an OpenJDK implementation on Windows,
here are two:
<ul>
<li><a href="https://adoptopenjdk.net/">AdoptOpenJDK</a> provides
prebuilt OpenJDK implementations for many systems.
<li><a href="https://developers.redhat.com/openjdk-install/">Red Hat
provides an implementation of OpenJDK for Windows</a>; you can go
there to get one.
</ul>

<p>
An alternative way to install Java would be to install an
<a href="https://www.oracle.com/java/technologies/javase-downloads.html"
>Oracle Java release</a>; make absolutely <i>certain</i> that you
comply with Oracle's licenses if you do this
(the free one, in particular, has many restrictions on how you
can legally use it).

<p>
<h3>(Recommended) Install git and a good text editor</h3>
<p>
The "git" program is a very widely used version control system
that supports global collaborative development of software and
software-like artifacts.
You can get a Windows version from the
<a href="https://git-scm.com/downloads">Git SCM downloads</a>.
<p>
You will almost certainly need a good text editor that can handle
large files, and preferably one that can handle POSIX standard line endings
(LF instead of Windows' CRLF).
The programs <tt>Notepad</tt> and <tt>Wordpad</tt> are <b>not</b>
good text editors.
There are many available; if you have no idea, you might try
<a href="https://notepad-plus-plus.org/">Notepad++</a>, which is small,
simple, and gets the job done.
Other options include <a href="https://atom.io/">Atom</a>,
<a href="https://www.vim.org/">vim</a>
<a href="https://code.visualstudio.com/">Microsoft Visual Studio Code</a>.

<p>
<h3>Install mmj2</h3>
<p>
Install mmj2; we recommend putting it in "C:\mmj2" on Windows.
Click on the "start" button, type "command", press ENTER, and then type:

<pre>
cd c:\
git clone https://github.com/digama0/mmj2.git
cd mmj2
</pre>


<p>
<h3>(Optional) Compile mmj2</h3>
<p>
You do not have to compile mmj2, because we provide a
precompiled jar file.
If you want to recompile it, here's what you do.
<p>
First, if you directly downloaded the mmj2 git repository, as recommended, run
the following from the top-level "mmj2" directory:
<pre>
git submodule init
git submodule update
</pre>
<p>
Now to actually compile it in Windows, presuming it's in its conventional
C:\mmj2 location, run:

<pre>
C:\mmj2\compile\windows\CompMMJ.bat
</pre>

<p>
This creates the <code>c:\mmj2\mmj2jar\mmj2jar.jar</code>
jar file, in addition to creating the various mmj2 class files.<br>

<p>
You should also compile the Javadoc for mmj2.
Use the Command Prompt window to execute the following command:<br>
<code>C:\mmj2\doc\windows\BuildDoc.bat</code>

<p>
<h3>(Recommended) Install the Metamath database set.mm</h3>
<p>
Put the database set.mm in its recommended place, which is
C:\set.mm for Windows.

<p>
<h3>(Recommended) Install Metamath-exe</h3>
<p>
Install metamath-exe in its conventional place,
which is C:\metamath for Windows.
<p>

<p>
<h3>(Optional) Create a desktop icon</h3>
<p>
On the start menu type
<code>C:\mmj2\mmj2jar</code> to open that directory
and then right-click on the <code>mmj2.bat</code> file.
Create a shortcut, then drag that to the desktop.
<p>
Note: Cygwin users should create a shortcut on the file
<code>mmj2-cygwin.bat</code> instead.


<p>
<h2>Unix/Linux</h2>
<p>

<p>
<h3>Install Java</h3>
<p>
First, install Java.
Tehcnically you only need a Java Runtime Environment (JRE)
implementation, but unless you're short of space it doesn't hurt to have a
full Java Development Kit (JDK) since the JDK includes a JRE.
Almost all widely-used systems have Java easily available as
an installable package.

For example:
<ul>
<li>On Ubuntu and Debian, install OpenJDK 11 as follows:
 <ul>
 <li>Install the JDK version 11 with: <tt>sudo apt install openjdk-11-jdk</tt>
 <li>You can install multiple different Java versions, e.g.,
     version 8 JDK is package <tt>openjdk-8-jdk</tt>, and the current
     default Java version is package <tt>default-jdk</tt>.
     Replace "-jdk" with "-jre" if you're short of disk space and <i>only</i>
     want the Java Runtime Environment (JRE).
     If you have more than one version, you can select between them using
     <tt>sudo update-alternatives --config java</tt>
 </ul>
<li>On Fedora, install OpenJDK 11 as follows:
  <ul>
  <li>Install JDK version 11 with:
      <tt>sudo dnf install java-11-openjdk.x86_64</tt>
  <li>You can replace "11" with "1.8" or "latest" to get version 8 or the
      latest version (respectively).
  </ul>
</ul>
<p>
An alternative way to install Java would be to install an
<a href="https://www.oracle.com/java/technologies/javase-downloads.html"
>Oracle Java release</a>; make absolutely <i>certain</i> that you
comply with Oracle's licenses if you do this.

<p>
<h3>Install git and a good text editor</h3>
<p>
Install git:
<ul>
<li>On Debian and Ubuntu this is <tt>sudo apt install git</tt>
<li>On Fedora this is <tt>sudo dnf install git</tt>
</ul>
<p>
Linux systems all come with at least one good text editor and
many alternatives.

<p>
<h3>Install mmj2</h3>
<p>
By convention mmj2 is placed in </tt>$HOME/mmj2</tt> though can be
placed anywhere. One way to do this is with these Terminal commands
to download it using git:

<pre>
cd
git clone https://github.com/digama0/mmj2.git
cd mmj2
</pre>

<p>
You can also download its current release using your web browser and going to
<https://github.com/digama0/mmj2>

<p>
<h3>(Optional) Compile mmj2</h3>
<p>
You do not have to compile mmj2, because we provide a
precompiled jar file.
If you want to recompile it, here's how.
<p>
First, if you directly downloaded the mmj2 git repository, as recommended, run
the following from the top-level "mmj2" directory:
<pre>
git submodule init
git submodule update
</pre>
<p>

Then run the following to actually compile it:
<pre>
compile/posix_compile
</pre>

<p>
This is created in the current directory.
You can move it to its final loation with:
<tt>mv mmj2.jar mmj2jar/</tt>

<p>
<h3>(Recommended) Install metamath-exe</h3>
<p>
You do not <i>have</i> to install the Metamath-exe
(C Metamath) implementation, but it is likely to be helpful.
The recommended location for it is "<tt>$HOME/metamath</tt>".

<p>
<h2>MacOS</h2>

<h3>Install Java</h3>
<p>
An easy way to install Java on MacOS is to go to
<li><a href="https://adoptopenjdk.net/">AdoptOpenJDK</a>, who provide
prebuilt OpenJDK implementations for many systems.
There's also an implementation available via <tt>brew</tt>.
<p>
An alternative way to install Java would be to install an
<a href="https://www.oracle.com/java/technologies/javase-downloads.html"
>Oracle Java release</a>; make absolutely <i>certain</i> that you
comply with Oracle's licenses if you do this
(the free one, in particular, has many restrictions on how you
can legally use it).

<h3>The rest</h3>
<p>
Now that you have Java installed,
follow the Linux/Unix/Cygwin instructions above.
We hope to expand this section soon.

<!-- TODO: eimm.zip
The Metamath <code><span
 style="font-weight: bold;">eimm.exe</span></code> (Export/Import mmj2
Proof Worksheets) program requires that it be located inside the
current directory, along with its helpers <code
 style="font-weight: bold;">eimmexp.cmd</code> and <code><span
 style="font-weight: bold;">eimmimp.cmd</span></code>. Therefore, <span
 style="text-decoration: underline;">if you plan to use mmj2 and
Metamath with Metamath's </span><code
 style="font-weight: bold; text-decoration: underline;">eimm.exe</code><span
 style="text-decoration: underline;"> utility, it is simplest to have
all of the software in a single directory.</span><br
 style="text-decoration: underline;">
<br>

- Copy the following files from <span style="font-weight: bold;">C:\metamath</span>
into <span style="font-weight: bold;">C:\mmj2jar</span><br>
<ul>
  <li style="font-weight: bold;"><code>metamath.exe</code></li>
  <li style="font-weight: bold;"><code>eimm.exe</code></li>
  <li style="font-weight: bold;"><code>eimmexp.cmd</code></li>
  <li style="font-weight: bold;"><code>eimmimp.cmd</code></li>
  <li style="font-weight: bold;">set.mm (and/or whatever .mm Metamath
database(s) you plan to use)<br>
  </li>
</ul>

-->


</body>
</html>