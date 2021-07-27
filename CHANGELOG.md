## [0.4.1] - 2021-06-14
- Add possibility to pass client to send/execute/destroy/receive functions, allowing for users to handle their own client and message queue
## [0.4.0] - 2021-06-14
- Timeout is now passed into init-reader-loop instead of client-start.
- Removed cheshire dependency. Now send and read interfaces require/provide string.

## [0.2.2] - 2019-10-08
### Changed
- Add support for close and logout calls.

