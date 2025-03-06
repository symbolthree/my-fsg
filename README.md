# myFSG

FSG report in Oracle EBS, a tool exists since 20+ years ago, is still the most widely used financial reporting tool. 
For mid-size companies which reluctant to buy third-party reporting tool, FSG is the only tool to create finance reports. 
Creating column set, row set, include or exclude ledger accounts, column alignment, etc drive many finance users crazy every day. 
Even a report is created, the Excel output still requires a lot of manual changes in order to meet usable standard.

**myFSG** is a Java program which can be executed in any Windows platform that it connects to Oracle EBS database,
and generate FSG report in natve Excel format using highly customizable style template. 
It supports Oracle EBS 11i, R12.1 and R12.2. No need to use Template Manager, XML Publisher, or even log on to Oracle EBS.

## Features

- Create FSG report directly from your desktop. No operations needed on the EBS side.
- Self-contained application. No Oracle client needed.
- Native Excel file (xlsx) output. Customized layout and format which fit your company style
- Written specific for FSG use in high performance

Former release can be found in [SoureForge](https://sourceforge.net/projects/myfsg/)
