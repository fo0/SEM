# SEM
Space Engineers Maintenance

An Application to Maintenance your Space Engineers Server.

Current usage:
Start the application with:
java -jar Path\Maintenance.jar -h

 -B,--backupPath <arg>               Path to Backup
 
 -c,--cleanup                        Cleanup active, needed for all cleanups
 
 -cb,--cleanupBeacon                 Cleanup noBeacon
 
 -cf,--cleanupFloatingObjects        Cleanup noPowered
 
 -cp,--cleanupNoPower                Cleanup noPowered
 
 -da,--deactivateAllFunctional       Deactivating all functional
 
 -dim,--disableIdleMovementTurrets   Deactivating the Idle-Movement on all turrets - for fixing bugs,lags
 
 -dpo,--disableProjectorsOnly        Deactivating Projectors for fixing bugs,lags
 
 -h,--help                           Show Help
 
 -S,--savePath <PathToFile>          Path to Savegame
 
 -v,--verbose                        Activate verbose

and so on

============== EXAMPLE ============  
Using a Savegame Path and Delete all Grids without Power+Beacon AND deactivate all functional blocks which are not needed.  
Example (without backup):

java -jar Path\Maintenance.jar -S "Path\To\Folder\" -c -cp -cb -da  

Example (with backup):

java -jar Path\Maintenance.jar -S "Path\To\Folder\of\SaveGame" -B "Path\to\Backup\Folder" -c -cp -cb -da  

Update:
Entire rework - Should work now as expected. Use the Example above. More adjustments should follow.
