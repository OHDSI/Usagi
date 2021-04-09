---
pagetitle: "ReadMe"
---
# Usagi Documentation Readme
This folder contains the raw and rendered documentation of WhiteRabbit.

## Contribute
Contributions to the documentation are very welcome and even a must when new features are implemented.
To update the documentation, edit one of the following markdown files or create a new markdown file:
 - [index.md](/docs/index.md)

[_site.yml](/docs/_site.yml) contains the configuration of the page.

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