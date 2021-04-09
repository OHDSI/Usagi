---
pagetitle: "ReadMe"
title: "Documentation readme"
output:
  html_document:
    toc: false
---

## Contribute
Contributions to this documentation are very welcome and even a must when new features are implemented.
To update the documentation, edit one of the following markdown files or create a new markdown file:

 - [index.md](https://github.com/ohdsi/usagi/blob/master/docs/index.md)
 - [usage.md](https://github.com/ohdsi/usagi/blob/master/docs/usage.md)
 - [installation.md](https://github.com/ohdsi/usagi/blob/master/docs/installation.md)
 
The configuration of the page can be found in [_site.yml](https://github.com/ohdsi/usagi/blob/master/docs/_site.yml). 

## Render html
To generate the site from markdown files, run the following R script with the `./docs` folder as working directory.
Run this in a standalone R session, this is orders of magnitude faster compared to running in RStudio.

```R
#devtools::install_github("ropenscilabs/icon")
library(rmarkdown)
rmarkdown::render_site()
```

The rendered html can be opened in any browser to inspect. 
When pushed to master, the html will be deployed as a github.io page and publically available.