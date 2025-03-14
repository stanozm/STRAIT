# Implementation of "Open source software reliability model with nonlinear fault detection and fault introduction"

# Get command line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 2) {
  stop("Usage: Rscript jinyong_wang_model.R input_data.csv output_results.csv")
}

data_file <- args[1]
results_file <- args[2]

# Suppress warnings
options(warn = -1)

# 1. Load data from input file
data <- read.csv(data_file, check.names = FALSE)
xvalues <- data$x
yvalues <- data$y

# Get the maximum observed fault count for initialization of 'a'
max_faults <- max(yvalues)

# 2. Define the sum calculation function
calc_sum_term_total <- function(omega, x, d, n=5) {
  total <- 0
  for (i in 0:n) {
    if (i > 170) break  # factorial limit in R
    
    log_term <- i * log(omega) + (i + d) * log(x) - lfactorial(i) - log(i + d)
    term <- exp(log_term)
    
    if (is.finite(term)) {
      total <- total + term
    }
  }
  return(total)
}

# 3. Define the model function
jinyong_wang_model <- function(x, a, theta, omega, beta, d, n=5) {
  # Parameter validation
  if (any(!is.finite(c(a, theta, omega, beta, d))) || 
      a <= 0 || theta <= 0 || omega <= 0 || beta <= 0 || d <= 0 || d >= 1) {
    return(rep(Inf, length(x)))
  }
  
  y_pred <- numeric(length(x))
  
  for (i in seq_along(x)) {
    xi <- x[i]
    
    if (omega * xi > 700) {  # exp() overflow threshold in R
      y_pred[i] <- a
    } else {
      sum_term <- calc_sum_term_total(omega, xi, d, n)
      
      denominator <- theta + exp(omega * xi)
      exp_term_exponent <- beta * xi^d + omega * xi
      
      if (exp_term_exponent > 700) {
        exp_term <- a
      } else {
        exp_term <- exp(exp_term_exponent)
      }
      
      y_pred_i <- (a / denominator) * (exp_term - beta * d * sum_term - 1)
      
      if (is.finite(y_pred_i) && y_pred_i >= 0) {
        y_pred[i] <- y_pred_i
      } else {
        return(rep(Inf, length(x)))
      }
    }
  }
  
  return(y_pred)
}

# 4. Create sum of squares function
ss_function <- function(params, x, y) {
  a <- params[1]
  theta <- params[2]
  omega <- params[3]
  beta <- params[4]
  d <- params[5]
  
  if (any(!is.finite(params)) || any(params <= 0) || d >= 1) {
    return(Inf)
  }
  
  predicted <- try(jinyong_wang_model(x, a, theta, omega, beta, d), silent = TRUE)
  
  if (inherits(predicted, "try-error") || any(!is.finite(predicted))) {
    return(Inf)
  }
  
  ss <- sum((y - predicted)^2)
  
  if (is.finite(ss) && ss > 0) {
    return(ss)
  } else {
    return(Inf)
  }
}

# 5. Set up adaptive initial values based on the data
# Create multiple starting parameter sets with adaptive 'a' values
# The asymptotic value 'a' is scaled relative to the maximum observed faults

# Define a function to create appropriate starting points based on data scale
create_start_points <- function(max_faults) {
  # Base 'a' value extrapolations
  a_values <- c(
    max_faults * 1.1,    # 10% more than observed maximum
    max_faults * 1.25,   # 25% more
    max_faults * 1.5,    # 50% more
    max_faults * 2.0,    # Double
    max_faults * 3.0     # Triple
  )
  
  # Ensure values are within reasonable range
  a_values <- pmin(a_values, 100000)  # Cap at 100,000 for numerical stability
  
  # Scale other parameters based on magnitude of 'a'
  scale_factor <- max(1, log10(max_faults) / log10(100))  # Scale factor based on order of magnitude
  
  # Create multiple starting points with different parameters
  list(
    c(a = a_values[1], theta = 1, omega = 0.05, beta = 0.005, d = 0.5),
    c(a = a_values[2], theta = 1.5 * scale_factor, omega = 0.03, beta = 0.008, d = 0.4),
    c(a = a_values[3], theta = 0.8 * scale_factor, omega = 0.07, beta = 0.003, d = 0.6),
    c(a = a_values[4], theta = 2 * scale_factor, omega = 0.01, beta = 0.01, d = 0.45),
    c(a = a_values[5], theta = 0.5 * scale_factor, omega = 0.1, beta = 0.002, d = 0.55)
  )
}

# Generate adaptive starting points
start_points <- create_start_points(max_faults)

# Define adaptive bounds - upper bound for 'a' depends on the data scale
a_upper_bound <- max(max_faults * 10, 100000)  # At least 10x the observed maximum, capped at 100,000
lower_bounds <- c(max_faults * 0.9, 1e-6, 1e-6, 1e-6, 1e-6)  # 'a' should be at least observed maximum
upper_bounds <- c(a_upper_bound, 100, 1, 0.1, 0.99)          # Increased upper bound for theta for large datasets

# 6. Track best result
best_result <- NULL
best_ss <- Inf
best_start_point <- NULL

# Try optimization with each starting point
for (i in 1:length(start_points)) {
  start_params <- start_points[[i]]
  
  # Try L-BFGS-B optimization
  result <- try(
    optim(
      par = start_params,
      fn = ss_function,
      x = xvalues, 
      y = yvalues,
      method = "L-BFGS-B",
      lower = lower_bounds,
      upper = upper_bounds,
      control = list(maxit = 5000, trace = 0)
    ),
    silent = TRUE
  )
  
  # Check if successful
  if (!inherits(result, "try-error") && is.finite(result$value)) {
    if (is.null(best_result) || result$value < best_ss) {
      best_ss <- result$value
      best_result <- result
      best_start_point <- i
    }
  }
}

# If no successful optimization, try Nelder-Mead as fallback
if (is.null(best_result)) {
  for (i in 1:length(start_points)) {
    start_params <- start_points[[i]]
    
    result <- try(
      optim(
        par = start_params,
        fn = ss_function,
        x = xvalues, 
        y = yvalues,
        method = "Nelder-Mead",
        control = list(maxit = 10000, trace = 0)
      ),
      silent = TRUE
    )
    
    if (!inherits(result, "try-error") && is.finite(result$value)) {
      if (is.null(best_result) || result$value < best_ss) {
        best_ss <- result$value
        best_result <- result
        best_start_point <- i
      }
    }
  }
}

# If still no successful optimization, use best starting parameters
if (is.null(best_result)) {
  # Find the best starting point
  best_ss_initial <- Inf
  best_start_index <- 1
  
  for (i in 1:length(start_points)) {
    start_params <- start_points[[i]]
    
    predicted <- try(jinyong_wang_model(xvalues, start_params[1], start_params[2], 
                                        start_params[3], start_params[4], start_params[5]), 
                     silent = TRUE)
    
    if (!inherits(predicted, "try-error") && all(is.finite(predicted))) {
      ss_value <- sum((yvalues - predicted)^2)
      
      if (ss_value < best_ss_initial) {
        best_ss_initial <- ss_value
        best_start_index <- i
      }
    }
  }
  
  best_result <- list(par = start_points[[best_start_index]], value = best_ss_initial)
}

# 7. Calculate fitted values and statistics with best parameters
final_params <- best_result$par

fitted_values <- jinyong_wang_model(
  xvalues, 
  final_params[1], 
  final_params[2], 
  final_params[3], 
  final_params[4], 
  final_params[5]
)

SSE <- sum((yvalues - fitted_values)^2)
SST <- sum((yvalues - mean(yvalues))^2)
R_squared <- 1 - SSE/SST
RMSE <- sqrt(mean((yvalues - fitted_values)^2))
AIC <- length(yvalues) * log(SSE/length(yvalues)) + 2 * 5
BIC <- length(yvalues) * log(SSE/length(yvalues)) + 5 * log(length(yvalues))

# 8. Write results to output file
results <- data.frame(
  a = final_params[1],
  theta = final_params[2],
  omega = final_params[3],
  beta = final_params[4],
  d = final_params[5],
  AIC = AIC,
  BIC = BIC,
  R_squared = R_squared
)

write.csv(results, file = results_file, row.names = FALSE)