---
layout: default
title: CAS - Maintenance Policy
---

# Maintenance Policy

This document describes the official CAS policy as it regards maintenance and management of released CAS server versions.

In particular, the following questions are addressed:

- How long should a CAS release be maintained?
- What is the appropriate scope for release maintenance once a release is retired?

## Policy

- CAS adopters **MAY EXPECT** a CAS release to be maintained for one calendar year, starting from the original release date.
- Maintenance during this year includes bug fixes, security patches and general upkeep of the release.
- Once the year is passed, maintenance of the release is **STRICTLY** limited to security patches and fixing vulnerabilities for another calendar year.
- The lifespan of a release **MAY** be extended beyond a single year, to be decided by the [CAS PMC](../Mailing-Lists.html) and the community at large when and where reasonable.

By “CAS Release”, we mean anything that is a minor release and above. (i.e. `4.1.x`, `4.2.x`, `5.0.0`, `5.1.0`, etc).

## EOL Schedule

The following CAS releases will transition into a security-patch mode (SPM) only and will be EOLed at the indicated dates.

| Release        | SPM Starting Date  | Full EOL  |
| -------------- |:-------------:| --------------:|
| `4.0.x`        | October 31st, 2016 | October 31st, 2017 |
| `4.1.x`        | January 31st, 2017 | January 31st, 2018 |
| `4.2.x`        | January 31st, 2017  | January 31st, 2018 |
| `5.0.x`        | September 30th, 2017  | September 30th, 2018 |

All past releases that are absent in the above table are considered EOLed.

