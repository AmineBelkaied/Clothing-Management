import { Injectable } from "@angular/core";
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot
} from "@angular/router";
import { StorageService } from "./strorage.service";
import { StringUtils } from "../utils/string-utils";

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(
        private storageService: StorageService,
        private router: Router) { }
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean | Promise<boolean> {

        if (this.storageService.isUserLoggedIn()) {
            if (route.data['role'] && !StringUtils.checkExistence(route.data['role'], this.storageService.getRoles())) {
                this.router.navigateByUrl('/');
                return false;
            }
            return true;
        }

        this.router.navigate(['/auth/login/'] + this.storageService.getTenantName())
        return false;
    }
}