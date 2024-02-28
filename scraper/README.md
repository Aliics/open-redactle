# scraper

The scraper has 2 modes: Generate and Update.

`Generate` will go to the wikipedia api, select some number of random articles, filter them out, convert them to a
usable format, and dump them into our S3 bucket. If we have already seen the article, we update it.

`Update` on the other hand, will look over our index in S3 and refetch all the data from wikipedia. It will also
filter anything out that no longer fits our criteria.
