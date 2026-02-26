<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use App\Domain\User\UserRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentUserRepository;
use App\Application\Contracts\JwtServiceInterface;
use App\Infrastructure\Services\JwtAuthService;
use App\Application\Contracts\EventDispatcherInterface;
use App\Infrastructure\Events\LaravelEventDispatcher;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentRefreshTokenRepository;
use App\Domain\Audit\AuditLogRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentAuditLogRepository;
use App\Application\Contracts\AuditLoggerInterface;
use App\Infrastructure\Logging\DatabaseAuditLogger;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public array $bindings = [
        UserRepositoryInterface::class => EloquentUserRepository::class,
        JwtServiceInterface::class => JwtAuthService::class,
        EventDispatcherInterface::class => LaravelEventDispatcher::class,
        RefreshTokenRepositoryInterface::class => EloquentRefreshTokenRepository::class,
        AuditLogRepositoryInterface::class => EloquentAuditLogRepository::class,
        AuditLoggerInterface::class => DatabaseAuditLogger::class,
    ];

    public function register(): void
    {
    }

    public function boot(): void
    {

    }
}
