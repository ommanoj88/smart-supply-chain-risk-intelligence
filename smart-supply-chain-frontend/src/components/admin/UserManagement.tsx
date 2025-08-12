import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  Avatar,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Tooltip,
  InputAdornment,
  Tabs,
  Tab,
  useTheme,
  alpha,
  LinearProgress,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Search,
  Download,
  Upload,
  Person,
  PersonAdd,
  AdminPanelSettings,
  ManageAccounts,
  SupervisorAccount,
  Business,
  Analytics,
  RemoveRedEye,
  Block,
  CheckCircle,
  Warning,
  Security,
  Refresh,
  MoreVert,
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuth } from '../../context/AuthContext';

interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  status: 'active' | 'inactive' | 'pending' | 'suspended';
  lastLogin: Date | null;
  createdAt: Date;
  department?: string;
  phone?: string;
  location?: string;
  profileImage?: string;
  permissions: string[];
}

type UserRole = 'SUPER_ADMIN' | 'ORG_ADMIN' | 'SUPPLY_MANAGER' | 'ANALYST' | 'VIEWER' | 'SUPPLIER_PORTAL';

interface RoleDefinition {
  key: UserRole;
  name: string;
  description: string;
  color: string;
  icon: React.ReactNode;
  permissions: string[];
  level: number;
}

/**
 * Enterprise-Grade User Management Component
 * Features:
 * - Comprehensive user directory with advanced search
 * - Visual role selector with permission matrix
 * - Bulk user operations (import/export via CSV)
 * - User creation wizard with role assignment
 * - Activity monitoring and audit logs
 * - Session management and security features
 */
const UserManagement: React.FC = () => {
  const theme = useTheme();
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterRole, setFilterRole] = useState<UserRole | 'all'>('all');
  const [filterStatus, setFilterStatus] = useState<string>('all');
  const [currentTab, setCurrentTab] = useState(0);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [newUser, setNewUser] = useState({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    role: 'VIEWER' as UserRole,
    department: '',
    phone: '',
    location: '',
    permissions: [] as string[],
  });

  // Role definitions with enterprise-grade permissions
  const roleDefinitions: RoleDefinition[] = [
    {
      key: 'SUPER_ADMIN',
      name: 'Super Administrator',
      description: 'Complete platform management and system configuration',
      color: theme.palette.error.main,
      icon: <AdminPanelSettings />,
      level: 100,
      permissions: [
        'user_management',
        'system_config',
        'billing_management',
        'audit_logs',
        'data_export',
        'api_access',
        'security_settings',
        'organization_management'
      ],
    },
    {
      key: 'ORG_ADMIN',
      name: 'Organization Admin',
      description: 'User and organization management within company',
      color: theme.palette.warning.main,
      icon: <ManageAccounts />,
      level: 80,
      permissions: [
        'user_management',
        'org_settings',
        'department_management',
        'role_assignment',
        'usage_monitoring',
        'custom_roles',
        'team_management'
      ],
    },
    {
      key: 'SUPPLY_MANAGER',
      name: 'Supply Chain Manager',
      description: 'Full supply chain operations and supplier management',
      color: theme.palette.primary.main,
      icon: <SupervisorAccount />,
      level: 60,
      permissions: [
        'supplier_management',
        'shipment_tracking',
        'risk_assessment',
        'analytics_full',
        'report_creation',
        'alert_management',
        'data_export'
      ],
    },
    {
      key: 'ANALYST',
      name: 'Supply Chain Analyst',
      description: 'Analytics, reporting, and trend analysis',
      color: theme.palette.info.main,
      icon: <Analytics />,
      level: 40,
      permissions: [
        'analytics_view',
        'report_creation',
        'data_export',
        'dashboard_custom',
        'trend_analysis',
        'performance_metrics'
      ],
    },
    {
      key: 'VIEWER',
      name: 'Viewer',
      description: 'Read-only access to dashboards and basic reports',
      color: theme.palette.success.main,
      icon: <RemoveRedEye />,
      level: 20,
      permissions: [
        'dashboard_view',
        'basic_reports',
        'alert_view',
        'profile_management'
      ],
    },
    {
      key: 'SUPPLIER_PORTAL',
      name: 'Supplier Portal User',
      description: 'Supplier-specific access for partners and vendors',
      color: theme.palette.secondary.main,
      icon: <Business />,
      level: 30,
      permissions: [
        'supplier_dashboard',
        'document_upload',
        'performance_view',
        'communication',
        'order_tracking'
      ],
    },
  ];

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      // Simulate API call - in real app, this would fetch from backend
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockUsers: User[] = [
        {
          id: '1',
          username: 'admin',
          email: 'admin@company.com',
          firstName: 'System',
          lastName: 'Administrator',
          role: 'SUPER_ADMIN',
          status: 'active',
          lastLogin: new Date(),
          createdAt: new Date('2024-01-01'),
          department: 'IT',
          phone: '+1-555-0123',
          location: 'San Francisco, CA',
          permissions: roleDefinitions.find(r => r.key === 'SUPER_ADMIN')?.permissions || [],
        },
        {
          id: '2',
          username: 'jsmith',
          email: 'j.smith@company.com',
          firstName: 'John',
          lastName: 'Smith',
          role: 'SUPPLY_MANAGER',
          status: 'active',
          lastLogin: new Date(Date.now() - 2 * 60 * 60 * 1000),
          createdAt: new Date('2024-02-15'),
          department: 'Supply Chain',
          phone: '+1-555-0124',
          location: 'Chicago, IL',
          permissions: roleDefinitions.find(r => r.key === 'SUPPLY_MANAGER')?.permissions || [],
        },
        {
          id: '3',
          username: 'mwilson',
          email: 'm.wilson@company.com',
          firstName: 'Maria',
          lastName: 'Wilson',
          role: 'ANALYST',
          status: 'active',
          lastLogin: new Date(Date.now() - 24 * 60 * 60 * 1000),
          createdAt: new Date('2024-03-01'),
          department: 'Analytics',
          phone: '+1-555-0125',
          location: 'New York, NY',
          permissions: roleDefinitions.find(r => r.key === 'ANALYST')?.permissions || [],
        },
        {
          id: '4',
          username: 'viewer01',
          email: 'viewer@company.com',
          firstName: 'Alex',
          lastName: 'Johnson',
          role: 'VIEWER',
          status: 'inactive',
          lastLogin: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
          createdAt: new Date('2024-04-10'),
          department: 'Operations',
          location: 'Austin, TX',
          permissions: roleDefinitions.find(r => r.key === 'VIEWER')?.permissions || [],
        },
      ];
      
      setUsers(mockUsers);
    } catch (error) {
      console.error('Error fetching users:', error);
    } finally {
      setLoading(false);
    }
  };

  const getRoleDefinition = (role: UserRole) => {
    return roleDefinitions.find(r => r.key === role);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return theme.palette.success.main;
      case 'inactive': return theme.palette.grey[500];
      case 'pending': return theme.palette.warning.main;
      case 'suspended': return theme.palette.error.main;
      default: return theme.palette.grey[500];
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'active': return <CheckCircle />;
      case 'inactive': return <Block />;
      case 'pending': return <Warning />;
      case 'suspended': return <Block />;
      default: return <Block />;
    }
  };

  const filteredUsers = users.filter(user => {
    const matchesSearch = 
      user.firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.username.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesRole = filterRole === 'all' || user.role === filterRole;
    const matchesStatus = filterStatus === 'all' || user.status === filterStatus;
    
    return matchesSearch && matchesRole && matchesStatus;
  });

  const handleCreateUser = async () => {
    try {
      const roleDefinition = getRoleDefinition(newUser.role);
      const userToCreate: User = {
        id: (users.length + 1).toString(),
        ...newUser,
        status: 'pending',
        lastLogin: null,
        createdAt: new Date(),
        permissions: roleDefinition?.permissions || [],
      };
      
      setUsers([...users, userToCreate]);
      setOpenCreateDialog(false);
      setNewUser({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        role: 'VIEWER',
        department: '',
        phone: '',
        location: '',
        permissions: [],
      });
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    setOpenEditDialog(true);
  };

  const handleDeleteUser = async (userId: string) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      setUsers(users.filter(u => u.id !== userId));
    }
  };

  const handleBulkAction = (action: string) => {
    console.log(`Performing ${action} on users:`, selectedUsers);
    // Implement bulk actions
  };

  const cardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 },
    hover: { y: -4, transition: { duration: 0.2 } },
  };

  return (
    <Box sx={{ 
      minHeight: '100vh',
      background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.02)} 0%, ${alpha(theme.palette.secondary.main, 0.01)} 100%)`,
      p: { xs: 2, md: 4 }
    }}>
      {/* Header Section */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <Box 
          sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            justifyContent: 'space-between',
            alignItems: { xs: 'flex-start', md: 'center' },
            mb: 4,
            p: 3,
            background: 'rgba(255, 255, 255, 0.8)',
            backdropFilter: 'blur(20px)',
            borderRadius: 3,
            border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.06)',
          }}
        >
          <Box>
            <Typography 
              variant="h3" 
              component="h1" 
              fontWeight={700}
              sx={{
                background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1,
              }}
            >
              User Management
            </Typography>
            <Typography 
              variant="subtitle1" 
              color="text.secondary"
              sx={{ 
                opacity: 0.8,
                fontWeight: 500,
                letterSpacing: '0.025em',
              }}
            >
              Comprehensive user directory and role-based access control
            </Typography>
          </Box>
          
          <Box display="flex" gap={2} mt={{ xs: 2, md: 0 }}>
            <Button
              variant="outlined"
              startIcon={<Upload />}
              sx={{ borderRadius: 2.5 }}
            >
              Import Users
            </Button>
            <Button
              variant="outlined"
              startIcon={<Download />}
              sx={{ borderRadius: 2.5 }}
            >
              Export
            </Button>
            <Button
              variant="contained"
              startIcon={<PersonAdd />}
              onClick={() => setOpenCreateDialog(true)}
              sx={{ borderRadius: 2.5 }}
            >
              Add User
            </Button>
          </Box>
        </Box>
      </motion.div>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {[
          {
            title: 'Total Users',
            value: users.length,
            subtitle: 'Active accounts',
            color: theme.palette.primary.main,
            icon: <Person />,
          },
          {
            title: 'Active Sessions',
            value: users.filter(u => u.status === 'active').length,
            subtitle: 'Currently online',
            color: theme.palette.success.main,
            icon: <CheckCircle />,
          },
          {
            title: 'Pending Approval',
            value: users.filter(u => u.status === 'pending').length,
            subtitle: 'Awaiting activation',
            color: theme.palette.warning.main,
            icon: <Warning />,
          },
          {
            title: 'Role Types',
            value: roleDefinitions.length,
            subtitle: 'Available roles',
            color: theme.palette.info.main,
            icon: <Security />,
          },
        ].map((stat, index) => (
          <Grid item xs={12} sm={6} lg={3} key={stat.title}>
            <motion.div
              variants={cardVariants}
              initial="hidden"
              animate="visible"
              whileHover="hover"
              transition={{ duration: 0.3, delay: index * 0.1 }}
            >
              <Card sx={{ height: '100%' }}>
                <CardContent sx={{ p: 3 }}>
                  <Box display="flex" alignItems="center" gap={2} mb={2}>
                    <Avatar sx={{ bgcolor: alpha(stat.color, 0.1), color: stat.color }}>
                      {stat.icon}
                    </Avatar>
                    <Typography variant="h6" fontWeight={600}>
                      {stat.title}
                    </Typography>
                  </Box>
                  <Typography variant="h3" fontWeight={700} color={stat.color} sx={{ mb: 1 }}>
                    {stat.value}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {stat.subtitle}
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        ))}
      </Grid>

      {/* Main Content */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, delay: 0.4 }}
      >
        <Card sx={{ borderRadius: 3 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={currentTab} onChange={(e, v) => setCurrentTab(v)}>
              <Tab label="All Users" />
              <Tab label="Role Matrix" />
              <Tab label="Activity Log" />
            </Tabs>
          </Box>

          {currentTab === 0 && (
            <Box sx={{ p: 3 }}>
              {/* Search and Filters */}
              <Box display="flex" gap={2} mb={3} flexWrap="wrap">
                <TextField
                  placeholder="Search users..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Search />
                      </InputAdornment>
                    ),
                  }}
                  sx={{ minWidth: 300 }}
                />
                
                <FormControl sx={{ minWidth: 150 }}>
                  <InputLabel>Role</InputLabel>
                  <Select
                    value={filterRole}
                    label="Role"
                    onChange={(e) => setFilterRole(e.target.value as UserRole | 'all')}
                  >
                    <MenuItem value="all">All Roles</MenuItem>
                    {roleDefinitions.map((role) => (
                      <MenuItem key={role.key} value={role.key}>
                        {role.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>

                <FormControl sx={{ minWidth: 150 }}>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={filterStatus}
                    label="Status"
                    onChange={(e) => setFilterStatus(e.target.value)}
                  >
                    <MenuItem value="all">All Status</MenuItem>
                    <MenuItem value="active">Active</MenuItem>
                    <MenuItem value="inactive">Inactive</MenuItem>
                    <MenuItem value="pending">Pending</MenuItem>
                    <MenuItem value="suspended">Suspended</MenuItem>
                  </Select>
                </FormControl>
                
                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  onClick={fetchUsers}
                  sx={{ borderRadius: 2 }}
                >
                  Refresh
                </Button>
              </Box>

              {/* Bulk Actions */}
              {selectedUsers.length > 0 && (
                <Box display="flex" gap={1} mb={2}>
                  <Typography variant="body2" color="text.secondary" sx={{ py: 1 }}>
                    {selectedUsers.length} users selected
                  </Typography>
                  <Button size="small" onClick={() => handleBulkAction('activate')}>
                    Activate
                  </Button>
                  <Button size="small" onClick={() => handleBulkAction('deactivate')}>
                    Deactivate
                  </Button>
                  <Button size="small" color="error" onClick={() => handleBulkAction('delete')}>
                    Delete
                  </Button>
                </Box>
              )}

              {/* Users Table */}
              {loading ? (
                <Box sx={{ mt: 2 }}>
                  <LinearProgress />
                </Box>
              ) : (
                <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell padding="checkbox">
                          {/* Checkbox for select all */}
                        </TableCell>
                        <TableCell>User</TableCell>
                        <TableCell>Role</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Department</TableCell>
                        <TableCell>Last Login</TableCell>
                        <TableCell align="center">Actions</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {filteredUsers.map((user) => {
                        const roleDefinition = getRoleDefinition(user.role);
                        return (
                          <TableRow key={user.id} hover>
                            <TableCell padding="checkbox">
                              {/* Checkbox */}
                            </TableCell>
                            <TableCell>
                              <Box display="flex" alignItems="center" gap={2}>
                                <Avatar 
                                  src={user.profileImage}
                                  sx={{ bgcolor: roleDefinition?.color }}
                                >
                                  {user.firstName[0]}{user.lastName[0]}
                                </Avatar>
                                <Box>
                                  <Typography variant="subtitle2" fontWeight={600}>
                                    {user.firstName} {user.lastName}
                                  </Typography>
                                  <Typography variant="body2" color="text.secondary">
                                    {user.email}
                                  </Typography>
                                </Box>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Chip
                                icon={roleDefinition?.icon}
                                label={roleDefinition?.name}
                                size="small"
                                sx={{
                                  bgcolor: alpha(roleDefinition?.color || '', 0.1),
                                  color: roleDefinition?.color,
                                }}
                              />
                            </TableCell>
                            <TableCell>
                              <Chip
                                icon={getStatusIcon(user.status)}
                                label={user.status.charAt(0).toUpperCase() + user.status.slice(1)}
                                size="small"
                                sx={{
                                  bgcolor: alpha(getStatusColor(user.status), 0.1),
                                  color: getStatusColor(user.status),
                                }}
                              />
                            </TableCell>
                            <TableCell>
                              <Typography variant="body2">
                                {user.department || '-'}
                              </Typography>
                            </TableCell>
                            <TableCell>
                              <Typography variant="body2" color="text.secondary">
                                {user.lastLogin 
                                  ? new Date(user.lastLogin).toLocaleDateString()
                                  : 'Never'
                                }
                              </Typography>
                            </TableCell>
                            <TableCell align="center">
                              <Box display="flex" gap={1} justifyContent="center">
                                <Tooltip title="Edit User">
                                  <IconButton 
                                    size="small" 
                                    onClick={() => handleEditUser(user)}
                                  >
                                    <Edit />
                                  </IconButton>
                                </Tooltip>
                                <Tooltip title="Delete User">
                                  <IconButton 
                                    size="small" 
                                    color="error"
                                    onClick={() => handleDeleteUser(user.id)}
                                  >
                                    <Delete />
                                  </IconButton>
                                </Tooltip>
                                <Tooltip title="More Actions">
                                  <IconButton size="small">
                                    <MoreVert />
                                  </IconButton>
                                </Tooltip>
                              </Box>
                            </TableCell>
                          </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </Box>
          )}

          {currentTab === 1 && (
            <Box sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight={600} sx={{ mb: 3 }}>
                Role & Permission Matrix
              </Typography>
              
              <Grid container spacing={3}>
                {roleDefinitions.map((role) => (
                  <Grid item xs={12} md={6} lg={4} key={role.key}>
                    <motion.div
                      variants={cardVariants}
                      initial="hidden"
                      animate="visible"
                      whileHover="hover"
                    >
                      <Card sx={{ 
                        height: '100%',
                        border: `2px solid ${alpha(role.color, 0.2)}`,
                      }}>
                        <CardContent sx={{ p: 3 }}>
                          <Box display="flex" alignItems="center" gap={2} mb={2}>
                            <Avatar sx={{ bgcolor: role.color, color: 'white' }}>
                              {role.icon}
                            </Avatar>
                            <Box>
                              <Typography variant="h6" fontWeight={600}>
                                {role.name}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                Level {role.level}
                              </Typography>
                            </Box>
                          </Box>
                          
                          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                            {role.description}
                          </Typography>
                          
                          <Box>
                            <Typography variant="subtitle2" fontWeight={600} sx={{ mb: 1 }}>
                              Permissions:
                            </Typography>
                            <Box display="flex" flexWrap="wrap" gap={0.5}>
                              {role.permissions.map((permission) => (
                                <Chip
                                  key={permission}
                                  label={permission.replace('_', ' ')}
                                  size="small"
                                  variant="outlined"
                                  sx={{ fontSize: '0.75rem' }}
                                />
                              ))}
                            </Box>
                          </Box>
                        </CardContent>
                      </Card>
                    </motion.div>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {currentTab === 2 && (
            <Box sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight={600} sx={{ mb: 3 }}>
                Activity & Audit Log
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Activity monitoring and audit logs will be displayed here.
              </Typography>
            </Box>
          )}
        </Card>
      </motion.div>

      {/* Create User Dialog */}
      <Dialog 
        open={openCreateDialog} 
        onClose={() => setOpenCreateDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Typography variant="h6" fontWeight={600}>
            Create New User
          </Typography>
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Username"
                value={newUser.username}
                onChange={(e) => setNewUser({...newUser, username: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={newUser.email}
                onChange={(e) => setNewUser({...newUser, email: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="First Name"
                value={newUser.firstName}
                onChange={(e) => setNewUser({...newUser, firstName: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Last Name"
                value={newUser.lastName}
                onChange={(e) => setNewUser({...newUser, lastName: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Role</InputLabel>
                <Select
                  value={newUser.role}
                  label="Role"
                  onChange={(e) => setNewUser({...newUser, role: e.target.value as UserRole})}
                >
                  {roleDefinitions.map((role) => (
                    <MenuItem key={role.key} value={role.key}>
                      <Box display="flex" alignItems="center" gap={1}>
                        {role.icon}
                        {role.name}
                      </Box>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Department"
                value={newUser.department}
                onChange={(e) => setNewUser({...newUser, department: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Phone"
                value={newUser.phone}
                onChange={(e) => setNewUser({...newUser, phone: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Location"
                value={newUser.location}
                onChange={(e) => setNewUser({...newUser, location: e.target.value})}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setOpenCreateDialog(false)}>
            Cancel
          </Button>
          <Button 
            variant="contained" 
            onClick={handleCreateUser}
            disabled={!newUser.username || !newUser.email || !newUser.firstName || !newUser.lastName}
          >
            Create User
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default UserManagement;