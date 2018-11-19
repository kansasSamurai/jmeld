# References
https://www.eclipse.org/emf/compare/documentation/latest/developer/developer-guide.html 
https://eclipsecon.org/summiteurope2006/presentations/ESE2006-EclipseModelingSymposium10_EMFCompareUtility.pdf 

# Feature Requests
From: https://sourceforge.net/p/jmeld/feature-requests/

1  [https://sourceforge.net/p/jmeld/feature-requests/1/]   
Support drag and drop files Next Release (example)  open    Kees Kuip   2007-05-21  2008-01-29  5    
Support drag and drop files in order to bypass the browse files dialog (Araxis style)

13  [https://sourceforge.net/p/jmeld/feature-requests/13/]
Implement as a Widget   None    open        2009-01-20  2009-01-20  5    

18  [https://sourceforge.net/p/jmeld/feature-requests/18/]
    The ability to compare two arbitrary Strings    None    open        2009-11-04  2009-11-04  5    

19  [https://sourceforge.net/p/jmeld/feature-requests/19/]
    Color Syntax hIghlighting   None    open        2009-11-05  2009-11-05  5    
    
23  Revert to defaults  Next Release (example)  open        2014-08-12  2014-08-12  1    
22  Linker/​rechter kolom kunnen wisselen   Next Release (example)  open        2014-08-12  2014-08-12  1    
21  Directory veld ook editable maken   Next Release (example)  open        2014-08-12  2014-08-12  1    
17  Work with git diff  None    open        2009-10-24  2009-10-24  5    
16  Allow to copy only one line of a group  None    open        2009-06-11  2009-06-11  5    
14  Add a "mark for later check" function   Next Release (example)  open        2009-06-11  2009-06-11  5    
11  vi alike editing    None    open        2008-12-10  2008-12-10  5    
10  Add hex diff    Next Release (example)  open        2008-10-23  2008-10-23  5    
5   Add an automatic merge  Next Release (example)  open        2008-10-13  2008-10-15  7    
4   Add a 3 windows comparison  release 3.5 open        2008-10-13  2008-10-15  7    


# Discussion
## Run Jmeld in Swing form - Feature Request
https://sourceforge.net/p/jmeld/discussion/547688/thread/594b540b/?limit=25#0cd3
(This is pretty much a rehash of Feature Request #13)

I would like a redirection to class name/interface so I can use JMeld visual diff panel in swing app without the JMeld external UI (tabs menus etc)

For example :
JPanel panel = new JPanel panel = new JPanel(new FlowLayout());
// probably not so easy...
JMeldDiffPanel jmeldDiff = JMeldDiffPanel(New File(a.txt), New File(a.txt));
panel.add(jmeldDiff);

This should work:

panel = new JMeldPanel();
panel.SHOW_TOOLBAR_OPTION.disable();
panel.SHOW_TABBEDPANE_OPTION.disable();
panel.openComparison(args[0], args[1]);
